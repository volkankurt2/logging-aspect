package com.odeal.logaspect.model;

public class LogInfo {
    public String url;
    public String queryString;
    public String method;
    public String authorization;
    public String statusCode;
    public Double responseTime;
    public String requestBody;
    public String responseBody;
    public String requestId;
    public String correlationId;
    public String requestStart;
    public String requestEnd;
    public String message;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "InfoLogModel{" +
                "url='" + url + '\'' +
                ", queryString='" + queryString + '\'' +
                ", method='" + method + '\'' +
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
}
