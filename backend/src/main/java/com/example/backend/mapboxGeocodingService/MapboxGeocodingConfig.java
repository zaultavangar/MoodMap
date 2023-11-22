package com.example.backend.mapboxGeocodingService;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MapboxGeocodingConfig {

  @Value("${mapbox.api.key}")
  private String apiKey;

  public String getApiKey() { return apiKey; }

}
