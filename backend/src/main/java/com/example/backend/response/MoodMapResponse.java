package com.example.backend.response;

import lombok.Data;

@Data
public class MoodMapResponse<T> {
    private int code;
    private String responseMessage;
    private T data;

    // Constructor
    public MoodMapResponse(int code, String responseMessage, T data) {
        this.code = code;
        this.responseMessage = responseMessage;
        this.data = data;
    }

    public MoodMapResponse() {
    }

    // success return response
    public static MoodMapResponse successResponse() {
        MoodMapResponse response = new MoodMapResponse();
        response.setCode(200);
        response.setResponseMessage("success");
        return response;
    }

    // fail return response
    public static MoodMapResponse failResponse(int code, String responseMessage) {
        MoodMapResponse response = new MoodMapResponse();
        response.code = code;
        response.responseMessage = responseMessage;
        response.data = null;
        return response;
    }
}