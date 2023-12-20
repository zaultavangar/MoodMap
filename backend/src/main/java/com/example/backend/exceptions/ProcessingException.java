package com.example.backend.exceptions;

/**
 * Custom exception class for handling general processing errors.
 */
public class ProcessingException extends Exception {

  /**
   * Constructor for ProcessingException.
   *
   * @param message The error message associated with the exception.
   */
  public ProcessingException(String message){
    super(message);
  }
}
