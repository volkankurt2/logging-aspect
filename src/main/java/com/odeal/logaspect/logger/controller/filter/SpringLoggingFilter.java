package com.odeal.logaspect.logger.controller.filter;

import com.odeal.logaspect.logger.controller.UniqueIDGenerator;
import com.odeal.logaspect.logger.controller.wrapper.SpringRequestWrapper;
import com.odeal.logaspect.logger.controller.wrapper.SpringResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SpringLoggingFilter extends OncePerRequestFilter {
    private static final String REQUEST_ID_HEADER_NAME = "X-Request-ID";
    private static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-ID";

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringLoggingFilter.class);

    private UniqueIDGenerator generator;

    public SpringLoggingFilter(UniqueIDGenerator generator) {
        this.generator = generator;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        generator.generateAndSetMDC(request);
        final SpringRequestWrapper wrappedRequest = new SpringRequestWrapper(request);

        final SpringResponseWrapper wrappedResponse = new SpringResponseWrapper(response);
        wrappedResponse.setHeader(REQUEST_ID_HEADER_NAME, MDC.get(REQUEST_ID_HEADER_NAME));
        wrappedResponse.setHeader(CORRELATION_ID_HEADER_NAME, MDC.get(CORRELATION_ID_HEADER_NAME));
        chain.doFilter(wrappedRequest, wrappedResponse);
    }
}