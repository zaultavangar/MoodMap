package com.example.backend.geocodingService;

import com.example.backend.exceptions.GeocodeApiException;
import javax.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@EnableCaching
public class GeocodingService {

//  private static final String API_BASE_URL = "https://api.mapbox.com/geocoding/v5/mapbox.places/";

  private static final String API_BASE_URL = "https://google-maps-geocoding.p.rapidapi.com/geocode/json";
  @Resource
  private GeocodingConfig config;

  @Resource
  private RestTemplate restTemplate;

  @Cacheable(value="locationCache", key="#location")
  public GeocodeResponse getGeocodeResultsForLocation(String location) throws GeocodeApiException {
    String encodedLocation = UriComponentsBuilder
        .fromUriString(location)
        .build()
        .toUriString();
    String url = UriComponentsBuilder
        .fromHttpUrl(API_BASE_URL)
        .queryParam("language", "en")
        .queryParam("rapidapi-key", config.getApiKey())
        .queryParam("rapidapi-host", "google-maps-geocoding.p.rapidapi.com")
        .queryParam("address", encodedLocation)
        .toUriString();

    System.out.println("GEOCODE URL: " + url);

    ResponseEntity<GeocodeResponse> response = restTemplate.getForEntity(url, GeocodeResponse.class);

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new GeocodeApiException("Error calling the Geocoding API: " + response.getStatusCode());
    }

    System.out.println("GEOCODE RESPONSE: " + response);
    System.out.println("GEOCODE RESPONSE BODY: " + response.getBody());
    return response.getBody(); // the geoJson

  }





}
