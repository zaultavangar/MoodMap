package com.example.backend.exceptions;

import com.example.backend.response.RestApiFailureResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * Global exception handler for REST APIs, providing consistent error responses.
 */
@ControllerAdvice
public class RestApiExceptionHandler {

  /**
   * Handles exceptions related to missing servlet request parameters. Helps override the
   * controller's default response when a required parameter is missing so that all responses
   * have the same shape (i.e. timestamp, status, result, data)
   *
   * @param ex The MissingServletRequestParameterException to handle.
   * @return A ResponseEntity with a RestApiFailureResponse containing the error details.
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<RestApiFailureResponse> handleMissingParams(MissingServletRequestParameterException ex) {
    String message = "Required parameter '" + ex.getParameterName() + "' is missing";
    RestApiFailureResponse response = new RestApiFailureResponse(400, message);
    return ResponseEntity.status(400).body(response); // Return ResponseEntity
  }
}
