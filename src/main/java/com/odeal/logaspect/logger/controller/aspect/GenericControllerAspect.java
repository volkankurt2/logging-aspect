package com.odeal.logaspect.logger.controller.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.odeal.logaspect.logger.controller.annotation.Logging;
import com.odeal.logaspect.logger.controller.annotation.NoLogging;
import com.odeal.logaspect.logger.controller.utils.JsonUtil;
import com.odeal.logaspect.logger.controller.utils.RequestUtil;
import com.odeal.logaspect.model.RequestData;
import com.odeal.logaspect.model.ResponseData;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.Date;

//@formatter:off

/**
 * This class is responsible for performing logging on Controller methods.
 * It used Spring AspectJ (not native Spring CGLib AOP) for weaving logging logic into matching controller methods.
 *
 * <p>This aspect uses two annotations - {@link Logging} and {@link NoLogging} to gain fine-grain control over
 * method logging behavior.
 *
 * @see <a href="http://docs.spring.io/spring/docs/current/spring-framework-reference/html/aop.html">
 * Spring Documentation on Aspect Oriented Programming with Spring
 * </a>
 */
//@formatter:on

@Aspect
public class GenericControllerAspect extends LoggerAspect implements ControllerAspect {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-ID";
    @NotNull
    private Logger LOG;

    @NotNull
    private JsonUtil jsonUtil;

    @NotNull
    private RequestUtil requestUtil;

    private ObjectMapper objectMapper = new ObjectMapper();

    public GenericControllerAspect() {
        this(
                org.slf4j.LoggerFactory.getLogger(String.class),
                new JsonUtil(),
                new RequestUtil()
        );
    }

    public GenericControllerAspect(
            @NotNull Logger LOG,
            @NotNull JsonUtil jsonUtil,
            @NotNull RequestUtil requestUtil) {
        this.LOG = LOG;
        this.jsonUtil = jsonUtil;
        this.requestUtil = requestUtil;
    }

    @Pointcut("@annotation(com.odeal.logaspect.logger.controller.annotation.Logging) " +
            "|| @target(com.odeal.logaspect.logger.controller.annotation.Logging)")
    public void methodOrClassLoggingEnabledPointcut() {
    }

    @Pointcut("!@annotation(com.odeal.logaspect.logger.controller.annotation.NoLogging)")
    public void methodLoggingNotDisabledPointcut() {
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) ||" +
            "within(@org.springframework.stereotype.Controller *)")
    public void allPublicControllerMethodsPointcut() {
    }

    @Around("allPublicControllerMethodsPointcut() "
            + "&& methodLoggingNotDisabledPointcut() "
            + "&& methodOrClassLoggingEnabledPointcut()")
    @Nullable
    public Object log(@NotNull ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = null;
        String returnType = null;
        RequestMapping methodRequestMapping = null;
        RequestMapping classRequestMapping = null;

        RequestData requestData = new RequestData();

        ResponseData responseData = new ResponseData();

        LOG.info("================= REQUEST START =================");
        try {
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            methodRequestMapping = methodSignature.getMethod().getAnnotation(RequestMapping.class);
            classRequestMapping = proceedingJoinPoint.getTarget().getClass().getAnnotation(RequestMapping.class);

            returnType = methodSignature.getReturnType().getName();
            requestData = logPreExecutionData(proceedingJoinPoint, methodRequestMapping);

        } catch (Exception e) {
            LOG.error("Exception occurred in pre-proceed logic", e);
        }

        StopWatch timer = new StopWatch();
        try {
            timer.start();
            result = proceedingJoinPoint.proceed();
        } finally {
            timer.stop();
            if (returnType != null) {
                Date requestEndDate = new Date();
                requestData.setRequestEnd(formatter.format(requestEndDate));

                responseData = logPostExecutionData(proceedingJoinPoint, timer, result, returnType, methodRequestMapping, classRequestMapping);
            }
        }

        LOG.info("");

        //LOG.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestData));
        //LOG.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseData));

        requestData.setRequestId(MDC.get(REQUEST_ID_HEADER_NAME));
        requestData.setCorrelationId(MDC.get(CORRELATION_ID_HEADER_NAME));

        LOG.info(objectMapper.writeValueAsString(requestData));
        LOG.info(objectMapper.writeValueAsString(responseData));

        LOG.info("");
        LOG.info("================= REQUEST END ===================");

        return result;
    }

    public RequestData logPreExecutionData(@NotNull ProceedingJoinPoint proceedingJoinPoint, @Nullable RequestMapping methodRequestMapping) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        String methodName = methodSignature.getName() + "()";
        Object argValues[] = proceedingJoinPoint.getArgs();
        String argNames[] = methodSignature.getParameterNames();
        String requestContext = requestUtil.getRequestContext().toString();
        Annotation annotations[][] = methodSignature.getMethod().getParameterAnnotations();

        StringBuilder arguments = new StringBuilder();

        if (argValues.length > 0) {
            arguments = logFunctionArguments(argNames, argValues, annotations, methodRequestMapping);
        }

        RequestData requestData = new RequestData();
        requestData.setUrl(requestContext);
        requestData.setRequestBody(arguments.toString());
        requestData.setMethod(methodName);

        Date requestDate = new Date();
        requestData.setRequestStart(formatter.format(requestDate));

        return requestData;
    }

    public ResponseData logPostExecutionData(
            @NotNull ProceedingJoinPoint proceedingJoinPoint,
            @NotNull StopWatch timer,
            @Nullable Object result,
            @NotNull String returnType,
            @Nullable RequestMapping methodRequestMapping,
            @Nullable RequestMapping classRequestMapping) {

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        String methodName = methodSignature.getName() + "()";

        boolean needsSerialization = isNeedSerilization(methodRequestMapping, classRequestMapping);
        StringBuilder postMessage = new StringBuilder();

        if (needsSerialization) {
            String resultClassName = result == null ? "null" : result.getClass().getName();
            resultClassName = returnType.equals("java.lang.Void") ? returnType : resultClassName;
            serialize(result, resultClassName, postMessage);
        } else {
            postMessage.append(result);
        }

        ResponseData responseData = new ResponseData();
        responseData.setResponseBody(postMessage.toString());
        responseData.setMethodName(methodName);
        responseData.setResponseTime((double) timer.getTime());

        return responseData;
    }

    @AfterThrowing(
            pointcut = "allPublicControllerMethodsPointcut() && "
                    + "methodLoggingNotDisabledPointcut() && "
                    + "methodOrClassLoggingEnabledPointcut()",
            throwing = "t")
    public void onException(@NotNull JoinPoint joinPoint, @NotNull Throwable t) {
        String methodName = joinPoint.getSignature().getName() + "()";
        LOG.info(methodName + " threw exception: [" + t + "]");

        LOG.info("================= REQUEST END ===================");
    }

    public void serialize(@Nullable Object object, @NotNull String objClassName, @NotNull StringBuilder logMessage) {
        boolean serializedSuccessfully = false;
        Exception exception = null;

        // this is to distinguish between methods returning null value and methods returning void.
        // Object arg is null in both cases but objClassName is not.
        if (objClassName.equals("java.lang.Void")) {
            logMessage.append("void");
            serializedSuccessfully = true;
        }

        // try serializing assuming a perfectly serializable object.
        if (!serializedSuccessfully) {
            try {
                logMessage.append(jsonUtil.toJson(object));
                serializedSuccessfully = true;
            } catch (Exception e) {
                exception = e;
            }
        }

        // try getting file size assuming object is a file type object
        if (!serializedSuccessfully) {
            long fileSize = -1;

            if (object instanceof ByteArrayResource) {
                fileSize = ((ByteArrayResource) object).contentLength();
            } else if (object instanceof MultipartFile) {
                fileSize = ((MultipartFile) object).getSize();
            }

            if (fileSize != -1) {
                logMessage.append("file of size:[").append(fileSize).append(" B]");
                serializedSuccessfully = true;
            }
        }

        // detect if its a mock object.
        if (!serializedSuccessfully && objClassName.toLowerCase().contains("mock")) {
            logMessage.append("Mock Object");
            serializedSuccessfully = true;
        }

        if (!serializedSuccessfully) {
            LOG.warn("Unable to serialize object of type [" + objClassName + "] for logging", exception);
        }
    }

    /**
     * Generated name-value pair of method's formal arguments. Appends the generated string in provided StringBuilder
     *
     * @param argNames  String[] containing method's formal argument names Order of names must correspond to order on arg
     *                  values in argValues.
     * @param argValues String[] containing method's formal argument values. Order of values must correspond to order on
     *                  arg names in argNames.
     */
    private StringBuilder logFunctionArguments(
            @NotNull String[] argNames,
            @NotNull Object[] argValues,
            @NotNull Annotation annotations[][],
            @Nullable RequestMapping methodRequestMapping) {
        StringBuilder stringBuilder = new StringBuilder();

        boolean someArgNeedsSerialization = false;

        if (methodRequestMapping != null) {
            for (String consumes : methodRequestMapping.consumes()) {
                if (consumes.equals(MediaType.APPLICATION_JSON_VALUE)) {
                    someArgNeedsSerialization = true;
                    break;
                }
            }
        }

        for (int i = 0, length = argNames.length; i < length; ++i) {
            boolean needsSerialization = false;

            if (argValues[i] instanceof ByteArrayResource || argValues[i] instanceof MultipartFile) {
                needsSerialization = true;
            } else {
                if (someArgNeedsSerialization) {
                    // We only need to serialize a param if @RequestBody annotation is found.
                    for (Annotation annotation : annotations[i]) {
                        if (annotation instanceof RequestBody) {
                            needsSerialization = true;
                            break;
                        }
                    }
                }
            }

            stringBuilder.append(argNames[i]).append(": [");
            if (needsSerialization) {
                String argClassName = argValues[i] == null ? "NULL" : argValues[i].getClass().getName();
                serialize(argValues[i], argClassName, stringBuilder);
            } else {
                stringBuilder.append(getScrubbedValue(argNames[i], argValues[i]));
            }
            stringBuilder.append("]").append(i == (length - 1) ? "" : ", ");
        }

        return stringBuilder;
    }

    /**
     * Returns scrubbed value for a given arg name-value. The original arg value is returned
     * if data scrubbing is disabled.
     *
     * @param argName  formal parameter name
     * @param argValue the parameter value
     * @return scrubbed value of argValue, or original value if data scrubbing is disabled
     */
    private Object getScrubbedValue(@NotNull String argName, @Nullable Object argValue) {
        Object argValueToUse = argValue;

        if (enableDataScrubbing && (paramBlacklist.contains(argName.toLowerCase()) || (paramBlacklistRegex != null && paramBlacklistRegex.matcher(argName).matches()))) {
            argValueToUse = scrubbedValue;
        }

        return argValueToUse;
    }

    public void setLOG(@NotNull Logger LOG) {
        this.LOG = LOG;
    }

    public void setJsonUtil(@NotNull JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    public void setRequestUtil(@NotNull RequestUtil requestUtil) {
        this.requestUtil = requestUtil;
    }

    public boolean isNeedSerilization(RequestMapping methodRequestMapping, RequestMapping classRequestMapping) {
        boolean needsSerialization = false;

        String produces[] = methodRequestMapping != null ? methodRequestMapping.produces() : new String[0];
        for (String produce : produces) {
            if (produce.equals(MediaType.APPLICATION_JSON_VALUE)) {
                needsSerialization = true;
                break;
            }
        }

        if (!needsSerialization) {
            produces = classRequestMapping != null ? classRequestMapping.produces() : new String[0];
            for (String produce : produces) {
                if (produce.equals(MediaType.APPLICATION_JSON_VALUE)) {
                    needsSerialization = true;
                    break;
                }
            }
        }

        return needsSerialization;
    }
}
