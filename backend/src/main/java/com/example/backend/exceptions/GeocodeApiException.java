package com.example.backend.exceptions;

/**
 * Custom exception class for handling errors related to the Geocode API.
 */
public class GeocodeApiException extends Exception{

  /**
   * Constructor for GeocodeApiException.
   *
   * @param message The error message associated with the exception.
   */
  public GeocodeApiException(String message){
    super(message);
  }
}
