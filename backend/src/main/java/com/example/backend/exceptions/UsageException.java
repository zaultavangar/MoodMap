package com.example.backend.exceptions;

/**
 * Custom exception class for handling errors related to incorrect usage of the application.
 */
public class UsageException extends Exception{

  /**
   * Constructor for UsageException.
   *
   * @param message The error message associated with the exception.
   */
  public UsageException(String message){
    super(message);
  }
}
