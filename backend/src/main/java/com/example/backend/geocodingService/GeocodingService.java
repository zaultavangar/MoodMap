package com.example.backend.geocodingService;

import com.example.backend.exceptions.GeocodeApiException;
import javax.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Service class for performing geocoding operations, using an external geocoding API.
 */
@Service
@EnableCaching
public class GeocodingService {

  private static final String API_BASE_URL = "https://google-maps-geocoding.p.rapidapi.com/geocode/json";
  @Resource
  private GeocodingConfig config;

  @Resource
  private RestTemplate restTemplate;

  /**
   * Retrieves geocoding results for a specified location. Cacheable based on location string passed in.
   *
   * @param location The location for which to obtain geocoding results.
   * @return A GeocodeResponse object containing geocoding data.
   * @throws GeocodeApiException if there is an error in calling the Geocoding API.
   */
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

    ResponseEntity<GeocodeResponse> response = restTemplate.getForEntity(url, GeocodeResponse.class);

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new GeocodeApiException("Error calling the Geocoding API: " + response.getStatusCode());
    }

    return response.getBody(); // the geoJson

  }





}
