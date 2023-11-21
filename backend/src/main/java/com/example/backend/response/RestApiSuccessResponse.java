package com.example.backend.response;

public class RestApiSuccessResponse<T> extends RestApiResponse {
    public RestApiSuccessResponse(T data) {
        super(200, "success", data);
    }
}
