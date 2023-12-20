package com.example.backend.response;

import java.util.HashMap;

/**
 * Represents a standardized API failure response.
 * Extends the generic RestApiResponse class with failure-specific details.
 */
public class RestApiFailureResponse extends RestApiResponse<Object> {

    /**
     * Constructor for creating a failure response.
     *
     * @param code The HTTP status code for the response.
     * @param responseMessage The message associated with the failure.
     */
    public RestApiFailureResponse(int code, String responseMessage) {
        super(code, responseMessage, new HashMap<>());
    }
}
