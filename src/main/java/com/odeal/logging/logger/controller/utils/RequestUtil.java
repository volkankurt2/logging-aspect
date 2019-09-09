package com.odeal.logging.logger.controller.utils;

import javax.servlet.http.HttpServletRequest;

import com.odeal.logging.logger.controller.bean.RequestContext;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtil {

    public RequestContext getRequestContext() {
        HttpServletRequest request = getCurrentHttpRequest();

        return new RequestContext()
                .add("url", getRequestUrl(request))
                .add("queryString", getQueryString(request))
                .add("method", getMethod(request));
    }

    @Nullable
    private String getRequestUrl(@Nullable HttpServletRequest request) {
        return request == null ? null : request.getRequestURL().toString();
    }

    @Nullable
    private String getQueryString(@Nullable HttpServletRequest request) {
        return request == null ? null : request.getQueryString();
    }

    @Nullable
    private String getMethod(@Nullable HttpServletRequest request) {
        return request == null ? null : request.getMethod();
    }

    @Nullable
    private HttpServletRequest getCurrentHttpRequest() {
        HttpServletRequest request = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {
            request = ((ServletRequestAttributes)requestAttributes).getRequest();
        }
        return request;
    }

}
