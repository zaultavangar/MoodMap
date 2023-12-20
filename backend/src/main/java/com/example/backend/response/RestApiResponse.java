package com.example.backend.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic class for standard API responses.
 * Can be extended or used directly for various types of API responses.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestApiResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private String result;
    private T data;

    /**
     * Constructor for creating an API response with a specific status, result, and data.
     *
     * @param status The HTTP status code.
     * @param result The result message.
     * @param data The data of type T to be included in the response.
     */
    public RestApiResponse(int status, String result, T data) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.result = result;
        this.data = data;
    }
}

