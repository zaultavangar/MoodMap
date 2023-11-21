package com.example.backend.mapboxGeocodingService;

import java.util.List;
import lombok.Data;

@Data
public class GeoJson {
  private String type = "FeatureCollection";
  private List<Feature> features;
}
