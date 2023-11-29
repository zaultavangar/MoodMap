package com.example.backend.nerService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HuggingFaceConfig {
  @Value("${huggingface.api.key}")
  private String apiKey;

  public String getApiKey(){
    return apiKey;
  }
}
