package com.example.backend.dbServices;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.entity.FeatureDTO;
import com.example.backend.entity.FeatureEntity;
import com.example.backend.geocodingService.GeoJsonGeometry;
import com.example.backend.repositories.FeatureRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeatureDbServiceTest {

  @Mock
  private FeatureRepository featureRepository;

  @InjectMocks
  private FeatureDbService featureDbService;

  /**
   * Tests the saving of a single feature entity to the database.
   * Verifies that the save method of featureRepository is called with any FeatureEntity class.
   */
  @Test
  void testSaveOne() {
    featureDbService.saveOne(new FeatureEntity());
    verify(featureRepository).save(any(FeatureEntity.class));
  }

  /**
   * Tests the retrieval of feature entities from the database.
   * Verifies that the getAllFeatures method of featureRepository is called.
   * Asserts that the size and content of the returned list match the expected values.
   */
  @Test
  void testGetFeatures() {
    GeoJsonGeometry franceGeometry = GeoJsonGeometry.builder()
        .type("Point")
        .coordinates(List.of(40.0, 60.0))
        .build();
    GeoJsonGeometry gazaGeometry = GeoJsonGeometry.builder()
        .type("Point")
        .coordinates(List.of(50.0, 10.0))
        .build();
    when(featureRepository.getAllFeatures()).thenReturn(
        List.of(
            FeatureEntity.builder()
                .type("Feature")
                .location("France")
                .properties(new HashMap<>())
                ._id(new ObjectId())
                .geoJsonGeometry(franceGeometry)
                .build()
            , FeatureEntity.builder()
                .type("Feature")
                .location("Gaza")
                .properties(new HashMap<>())
                ._id(new ObjectId())
                .geoJsonGeometry(gazaGeometry)
                .build()
        )
    );

    List<FeatureDTO> features = featureDbService.getFeatures();

    verify(featureRepository).getAllFeatures();
    assertEquals(2, features.size());
    assertEquals("Feature", features.get(0).getType());
    assertEquals(new HashMap<>(), features.get(0).getProperties());
    assertEquals(franceGeometry, features.get(0).getGeometry());
    assertEquals("Feature", features.get(1).getType());
    assertEquals(new HashMap<>(), features.get(1).getProperties());
    assertEquals(gazaGeometry, features.get(1).getGeometry());
  }

  /**
   * Tests finding a feature entity by its location.
   * Verifies that the findByLocation method of featureRepository is called with the specified location.
   */
  @Test
  void testFindFeatureByLocation() {
    featureDbService.findFeatureByLocation("Gaza");
    verify(featureRepository).findByLocation("Gaza");
  }

  /**
   * Tests the creation of a FeatureEntity object.
   * Verifies that the created FeatureEntity matches the expected structure and content.
   */
  @Test
  void testCreateFeatureEntity() {
    GeoJsonGeometry geometry = GeoJsonGeometry.builder()
        .type("Point")
        .coordinates(List.of(50.0, 10.0))
        .build();
    FeatureEntity expectedFeature = FeatureEntity.builder()
        .type("Feature")
        .geoJsonGeometry(geometry)
        .properties(Map.of("location", "Gaza"))
        .location("Gaza")
        .build();

    FeatureEntity retrievedFeatureEntity = featureDbService.createFeatureEntity(
        50.0,
        10.0,
        "Gaza");

    assertEquals(expectedFeature, retrievedFeatureEntity);

  }
}