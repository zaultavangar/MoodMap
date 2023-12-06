package com.example.backend.geocodingService;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

// STATUS: Not tested
@Configuration
public class GeocodingConfig {


//  @Value("${mapbox.api.key}")
  @Value("${rapidapi.key}")
  private String apiKey;

  public String getApiKey() { return apiKey; }

}
