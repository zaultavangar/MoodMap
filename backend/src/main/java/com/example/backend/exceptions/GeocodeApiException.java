package com.example.backend.exceptions;

public class GeocodeApiException extends Exception{
  public GeocodeApiException(String message){
    super(message);
  }
}
