package com.example.backend.geocodingService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.exceptions.GeocodeApiException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GeocodingServiceTest {

  private static final String API_URL = "https://google-maps-geocoding.p.rapidapi.com/geocode/json";

  @InjectMocks GeocodingService geocodingService;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private GeocodingConfig config;

  /**
   * Tests successful geocoding response for a given location. Mocks the RestTemplate
   * call and asserts that no exception is thrown and the response matches expected values.
   */
  @Test
  void testGetGeocodeResultsForLocationSuccess(){
    GeocodeGeometry geometry = new GeocodeGeometry(Map.of(
        "lat", 40.0, "lng", 60.0
    ));
    GeocodeResult geocodeResult = new GeocodeResult("France", geometry);
    GeocodeResponse geocodeResponse = new GeocodeResponse(List.of(geocodeResult));
    ResponseEntity<GeocodeResponse> mockResponse = new ResponseEntity<>(geocodeResponse, HttpStatus.OK);

    when(restTemplate.getForEntity(any(String.class), eq(GeocodeResponse.class)))
        .thenReturn(mockResponse);

    assertDoesNotThrow(() -> {
      GeocodeResponse response = geocodingService.getGeocodeResultsForLocation("France");

      assertEquals(1, response.getResults().size());
      assertEquals("France", response.getResults().get(0).getFormatted_address());
      assertEquals(geometry, response.getResults().get(0).getGeometry());
    });
    verify(restTemplate).getForEntity(any(String.class), eq(GeocodeResponse.class));
  }

  /**
   * Tests the handling of a failed geocoding response. Mocks a failed response from RestTemplate
   * and asserts that a GeocodeApiException is thrown by the service.
   */
  @Test
  void testGeoGeocodeResultsForLocationFailure(){
    GeocodeGeometry geometry = new GeocodeGeometry(Map.of(
        "lat", 40.0, "lng", 60.0
    ));

    ResponseEntity<GeocodeResponse> mockFailedResponse = ResponseEntity.internalServerError().build();

    when(restTemplate.getForEntity(any(String.class), eq(GeocodeResponse.class)))
        .thenReturn(mockFailedResponse);

    assertThrows(GeocodeApiException.class, () ->
      geocodingService.getGeocodeResultsForLocation("France")
    );

    verify(restTemplate).getForEntity(any(String.class), eq(GeocodeResponse.class));
  }
}