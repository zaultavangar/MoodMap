package com.example.backend.exceptions;

/**
 * Custom exception class for handling errors related to the HuggingFace API.
 */
public class HuggingFaceApiException extends Exception{

  /**
   * Constructor for HuggingFaceApiException.
   *
   * @param message The error message associated with the exception.
   */
  public HuggingFaceApiException(String message){
    super(message);
  }
}
