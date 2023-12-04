package com.example.backend.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class RestApiSuccessResponse<T> extends RestApiResponse<T> {
    public RestApiSuccessResponse(T data) {
        super(200, "success", data);
    }
}
