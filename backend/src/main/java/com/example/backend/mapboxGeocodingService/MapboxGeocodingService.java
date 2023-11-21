package com.example.backend.mapboxGeocodingService;

import com.example.backend.exceptions.MapboxApiException;
import java.io.IOException;
import javax.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MapboxGeocodingService {

  private static final String API_BASE_URL = "https://api.mapbox.com/geocoding/v5/mapbox.places/";

  @Resource
  private MapboxGeocodingConfig config;

  @Resource
  private RestTemplate restTemplate;

  public GeoJson getFeatureForLocation(String location) throws IOException, MapboxApiException {
    String encodedLocation = UriComponentsBuilder
        .fromUriString(location)
        .build()
        .toUriString();

    String url = UriComponentsBuilder
        .fromHttpUrl(API_BASE_URL + encodedLocation + ".json")
        .queryParam("access_token", config.getApiKey())
        .queryParam("limit", 1)
        .toUriString();

    ResponseEntity<GeoJson> response = restTemplate.getForEntity(url, GeoJson.class);

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new MapboxApiException("Error calling the Mapbox Geocoding API: " + response.getStatusCode());
    }

    GeoJson geoJson = response.getBody();

    return geoJson;
  }
}
