package com.example.backend.geocodingService;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for geocoding services, holding API-related configurations.
 */
@Configuration
public class GeocodingConfig {

  @Value("${rapidapi.key}")
  private String apiKey;

  /**
   * Returns the API key for the geocoding service.
   *
   * @return A String containing the API key.
   */
  public String getApiKey() { return apiKey; }

}
