package com.example.backend.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestApiResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private String result;
    private T data;

    public RestApiResponse(int status, String result, T data) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.result = result;
        this.data = data;
    }
}

