package com.odeal.logging.model;

public class RequestData {
    public String url;
    public String method;
    public String requestBody;
    public String requestId;
    public String correlationId;
    public String requestStart;
    public String requestEnd;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
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

    @Override
    public String toString() {
        return "RequestData{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", requestBody='" + requestBody + '\'' +
                ", requestId='" + requestId + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", requestStart='" + requestStart + '\'' +
                ", requestEnd='" + requestEnd + '\'' +
                '}';
    }
}
