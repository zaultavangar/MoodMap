package com.example.backend.entity;

import com.example.backend.mapboxGeocodingService.Geometry;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "features")
public class FeatureEntity {
    
    @Id
    private ObjectId _id;

    private String type;

    @Indexed(unique = true)
    private String location;

    private Map<String, String> properties = new HashMap<>();

    @GeoSpatialIndexed
    private Geometry geometry;
    public void setPropertiesLocation(String location) {
      properties.put("location", location);
    }

    public String getPropertiesLocation() {
        return properties.getOrDefault("location", "");
    }


}
