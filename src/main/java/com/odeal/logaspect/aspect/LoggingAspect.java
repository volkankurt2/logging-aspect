package com.odeal.logaspect.aspect;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.odeal.logaspect.model.LogInfo;
import com.odeal.logaspect.wrapper.SpringRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    /**
     * Pointcut that matches all Spring beans in the application's main packages.
     */
    @Pointcut("within(com.odeal.logaspect..*)" +
            " || within(com.odeal.logaspect.service..*)" +
            " || within(com.odeal.logaspect.controller..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Pointcut("within(org.springframework.web.bind.annotation.RestController..*)")
    public void restController() {

    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping() {

    }

    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice
     * @param e         exception
     */
    @AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), e.getCause() != null ? e.getCause() : "NULL");
    }

    /**
     * Advice that logs when a method is entered and exited.
     *
     * @param joinPoint join point for advice
     * @return result
     * @throws Throwable throws IllegalArgumentException
     */

    @Around("within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Date requestDate = new Date();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        final SpringRequestWrapper wrappedRequest = new SpringRequestWrapper(request);

        try {
            LogInfo logInfo = new LogInfo();
            logInfo.setRequestStart(formatter.format(requestDate));
            logInfo.setMethod(wrappedRequest.getMethod());
            logInfo.setUrl(wrappedRequest.getRequestURI());
            logInfo.setQueryString(wrappedRequest.getQueryString());
            logInfo.setRequestBody(Arrays.toString(joinPoint.getArgs()));


            Object result = joinPoint.proceed();
            Date responseDate = new Date();
            long duration = System.currentTimeMillis() - start;
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

            logInfo.setResponseTime((double) duration);
            logInfo.setRequestEnd(formatter.format(responseDate));
            logInfo.setResponseBody(result.toString());
            logInfo.setStatusCode(String.valueOf(response.getStatus()));

            log.info("*************************** START REQUEST ***************************");
            log.info(logInfo.toString());
            log.info("*************************** END REQUEST *****************************");

            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }
}
