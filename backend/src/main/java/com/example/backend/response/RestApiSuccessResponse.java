package com.example.backend.response;

/**
 * Represents a standardized API success response.
 * Extends the generic RestApiResponse class with success-specific details.
 */
public class RestApiSuccessResponse<T> extends RestApiResponse<T> {

    /**
     * Constructor for creating a success response.
     *
     * @param data The data to include in the success response.
     */
    public RestApiSuccessResponse(T data) {
        super(200, "success", data);
    }
}
