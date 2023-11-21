package com.example.backend.entity;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.client.model.geojson.Geometry;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "features")

public class FeatureEntity {
    
    @Id
    private ObjectId _id;

    private String type;

    private Map<String, String> properties = new HashMap<>();

    @GeoSpatialIndexed
    private Geometry geometry;

    public void setText(String text) {
      properties.put("text", text);
    }

    public String getText() {
        return properties.getOrDefault("text", null);
    }

    public void setPlaceName(String placeName) {
        properties.put("placeName", placeName);
    }

    public String getPlaceName() {
        return properties.getOrDefault("placeName", null);
    }

}
