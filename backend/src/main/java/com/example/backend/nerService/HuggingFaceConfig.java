package com.example.backend.nerService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the Hugging Face API.
 * Holds the API key for the Hugging Face service.
 */
@Configuration
public class HuggingFaceConfig {
  @Value("${huggingface.api.key}")
  private String apiKey;

  /**
   * Retrieves the API key for Hugging Face.
   *
   * @return The API key as a String.
   */
  public String getApiKey(){
    return apiKey;
  }
}
