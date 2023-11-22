package com.example.backend.sentimentAnalysisService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HuggingFaceConfig {
  @Value("${huggingface.api.key}")
  private String apiKey;

  public String getApiKey(){
    return apiKey;
  }
}
