package com.example.backend.geocodingService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Represents a single result from a geocoding request, including the formatted address and geometry.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeocodeResult {
  private String formatted_address;
  private GeocodeGeometry geometry;
}
