package com.odeal.logaspect.model;

public class ResponseData {

    public String statusCode;
    public Double responseTime;
    public String responseBody;
    public String responseEnd;
    public String methodName;
    public String message;

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

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponseEnd() {
        return responseEnd;
    }

    public void setResponseEnd(String responseEnd) {
        this.responseEnd = responseEnd;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "statusCode='" + statusCode + '\'' +
                ", responseTime=" + responseTime +
                ", responseBody='" + responseBody + '\'' +
                ", responseEnd='" + responseEnd + '\'' +
                ", methodName='" + methodName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
