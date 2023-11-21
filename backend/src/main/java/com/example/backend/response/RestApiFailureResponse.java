package com.example.backend.response;

public class RestApiFailureResponse extends RestApiResponse {

    public RestApiFailureResponse(int code, String responseMessage) {
        super(code, responseMessage, null);
    }
}
