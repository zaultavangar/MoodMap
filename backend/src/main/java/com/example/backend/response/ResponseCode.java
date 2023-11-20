package com.example.backend.response;

public enum ResponseCode {

    // Other enum instances can be defined here, if needed
    ERROR_CALLING_GUARDIAN_CONTENT_API(1001, "Error occurs when calling guardian content api");

    private final int code;
    private final String errorMessage;

    // Constructor
    ResponseCode(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    // Getters
    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}