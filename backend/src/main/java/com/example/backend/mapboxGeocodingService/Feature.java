package com.example.backend.mapboxGeocodingService;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class Feature {
  private String id;
  private String type = "Feature";
  private List<String> place_type;
  private Map<String, String> properties;
  private String text;
  private String place_name;
  private Geometry geometry;
}
