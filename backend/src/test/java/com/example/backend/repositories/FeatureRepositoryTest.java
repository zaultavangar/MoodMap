package com.example.backend.repositories;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.example.backend.entity.ArticleEntity;
import com.example.backend.entity.FeatureEntity;
import com.example.backend.geocodingService.GeoJsonGeometry;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@DataMongoTest
class FeatureRepositoryTest {

  @Autowired
  private FeatureRepository featureRepository;

  private final FeatureEntity FEATURE_1 = FeatureEntity.builder()
      ._id(new ObjectId())
      .type("Feature")
      .location("France")
      .geoJsonGeometry(GeoJsonGeometry.builder()
          .type("Point")
          .coordinates(List.of(2.2137, 46.2276))
          .build())
      .properties(new HashMap<>())
      .build();

  private final FeatureEntity FEATURE_2 = FeatureEntity.builder()
      ._id(new ObjectId())
      .type("Feature")
      .location("USA")
      .geoJsonGeometry(GeoJsonGeometry.builder()
          .type("Point")
          .coordinates(List.of(-95.7129, 37.0902))
          .build())
      .properties(new HashMap<>())
      .build();

  @BeforeEach
  void setUp() {
    featureRepository.saveAll(List.of(FEATURE_1, FEATURE_2));
  }

  @AfterEach
  void tearDown() {
    featureRepository.deleteAll();
  }

  @Test
  void testFindByLocationSuccessfullyFindsFeature() {
    Optional<FeatureEntity> featureEntity = featureRepository.findByLocation("France");

    assertThat(featureEntity.isPresent()).isTrue();
    assertDoesNotThrow(featureEntity::get);
    assertThat(featureEntity.get().get_id()).isEqualTo((FEATURE_1.get_id()));
  }

  @Test
  void testFindByLocationReturnsNoFeaturesWhenNoMatchIsFound() {
    Optional<FeatureEntity> featureEntity = featureRepository.findByLocation("Germany");

    assertThat(featureEntity.isPresent()).isFalse();

  }
}