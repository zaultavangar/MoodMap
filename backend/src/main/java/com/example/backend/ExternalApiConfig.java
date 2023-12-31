package com.example.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for external APIs.
 * Provides a RestTemplate bean for HTTP requests.
 */
@Configuration
public class ExternalApiConfig {
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
