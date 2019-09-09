package com.odeal.logaspect.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class InfoLog {
    public String url;
    public String methodName;
    public String authorization;
    public String statusCode;
    public Double responseTime;
    public String requestBody;
    public String responseBody;
    public String requestId;
    public String correlationId;
    public String requestStart;
    public String requestEnd;
    public List<String> message;

    private DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");

    public InfoLog(){
        message = new ArrayList<String>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Double responseTime) {
        this.responseTime = responseTime;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getRequestStart() {
        return requestStart;
    }

    public void setRequestStart(String requestStart) {
        this.requestStart = requestStart;
    }

    public String getRequestEnd() {
        return requestEnd;
    }

    public void setRequestEnd(String requestEnd) {
        this.requestEnd = requestEnd;
    }

    public List<String> getMessage() {
        return message;
    }

    public void addMessage(String message) {
        this.message.add(dateFormat.format(Calendar.getInstance().getTime()) + " - " +message);
    }

    @Override
    public String toString() {
        return "InfoLog{" +
                "url='" + url + '\'' +
                ", methodName='" + methodName + '\'' +
                ", authorization='" + authorization + '\'' +
                ", statusCode='" + statusCode + '\'' +
                ", responseTime=" + responseTime +
                ", requestBody='" + requestBody + '\'' +
                ", responseBody='" + responseBody + '\'' +
                ", requestId='" + requestId + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", requestStart='" + requestStart + '\'' +
                ", requestEnd='" + requestEnd + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
