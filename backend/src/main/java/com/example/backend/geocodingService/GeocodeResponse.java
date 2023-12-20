package com.example.backend.geocodingService;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Encapsulates the response received from a geocoding service, containing a list of geocode results.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeocodeResponse {
  private List<GeocodeResult> results;
}
