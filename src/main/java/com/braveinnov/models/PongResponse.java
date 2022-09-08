package com.braveinnov.models;

public class PongResponse {
    String code;
    String message;

    public PongResponse() {
    }
    public PongResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}