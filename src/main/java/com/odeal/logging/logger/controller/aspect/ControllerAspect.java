package com.odeal.logging.logger.controller.aspect;

import javax.validation.constraints.NotNull;

import com.odeal.logging.logger.controller.utils.JsonUtil;
import com.odeal.logging.model.InfoLog;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;

public interface ControllerAspect {

    /**
     * This pointcut is for joinpoint corresponding to all public methods in controller
     */
    void allPublicControllerMethodsPointcut();

    void methodOrClassLoggingEnabledPointcut();

    void methodLoggingNotDisabledPointcut();

    @Nullable
    Object log(@NotNull ProceedingJoinPoint proceedingJoinPoint) throws Throwable;

    /**
     * Logs following data on INFO level about executing method -
     * <ul>
     * <li>Method name</li>
     * <li>Method argument name-value pair</li>
     * <li>RequestData details including referrer, HTTP method, URI and username</li>
     * </ul>
     *
     * @param proceedingJoinPoint the joinpoint object representing the target method
     */
    void logPreExecutionData(
            @NotNull ProceedingJoinPoint proceedingJoinPoint,
            @Nullable RequestMapping methodRequestMapping, InfoLog infoLog);

    /**
     * Logs following data on INFO level about executed method -
     * <ul>
     * <li>Execution time of method in milliseconds</li>
     * </ul>
     *
     * Logs following data on DEBUG level about executed method -
     * <ul>
     * <li>JSON representation of object returned by method</li>
     * </ul>
     *
     * @param proceedingJoinPoint the jointpoint denoting the executed method
     * @param timer {@link StopWatch} object containing execution time of method
     * @param result the object returned by executed method
     * @param returnType class name of object returned by executed method
     */
    void logPostExecutionData(
            @NotNull ProceedingJoinPoint proceedingJoinPoint,
            @NotNull StopWatch timer,
            @Nullable Object result,
            @NotNull String returnType,
            @Nullable RequestMapping methodRequestMapping,
            @Nullable RequestMapping classRequestMapping,
            InfoLog infoLog);

    /**
     * Logs any exception thrown by method. This aspect is executed <b>AFTER</b> the exception has been thrown, so one
     * cannot swallow it over here.
     */
    void onException(@NotNull JoinPoint joinPoint, @NotNull Throwable t);

    /**
     * Converts given object to its JSON representation via {@link JsonUtil}. The serialized JSON to then appended to
     * passed {@link StringBuilder} instance.
     *
     * <p>
     * Some exceptional cases -
     * <ol>
     * <li>For objects of file type the file size in bytes is printed.</li>
     * <li>Mocked objects are not serialized. Instead a message is printed indicating that the object is a mocked
     * object. Mocked objects are detected by presence of 'mock' substring in their class name.</li>
     * </ol>
     *
     * @param object the object to serialize
     * @param objClassName object's class name.
     * @param logMessage {@link StringBuilder} instance to append serialized JSON.
     */
    void serialize(@Nullable Object object, @NotNull String objClassName, @NotNull StringBuilder logMessage);

}
