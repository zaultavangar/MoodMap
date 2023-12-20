package com.example.backend.exceptions;

/**
 * Custom exception class for handling errors related to the Guardian API.
 */
public class GuardianApiException extends Exception{

  /**
   * Constructor for GuardianApiException.
   *
   * @param message The error message associated with the exception.
   */
  public GuardianApiException(String message){
    super(message);
  }
}
