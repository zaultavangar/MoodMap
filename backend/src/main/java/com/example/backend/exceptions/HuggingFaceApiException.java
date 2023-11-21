package com.example.backend.exceptions;

public class HuggingFaceApiException extends Exception{

  public HuggingFaceApiException(String message){
    super(message);
  }
}
