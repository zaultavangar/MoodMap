package com.example.backend.geocodingService;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the geometry of a geocoding result, primarily containing the location coordinates.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeocodeGeometry {
  Map<String, Double> location;

}
