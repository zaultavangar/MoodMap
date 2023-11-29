package com.example.backend.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FeatureEntityTest {

  @Test
  void testSetPropertiesLocationSuccessfullyAddsLocation() {
    FeatureEntity feature = FeatureEntity.builder()
        .properties(new HashMap<>())
        .build();
    feature.setPropertiesLocation("China");
    assertThat(feature.getProperties().get("location")).isEqualTo("China");
  }
  @Test
  void testSetDoublePropertySuccessfullyUpdatesProperties() {
    FeatureEntity feature = FeatureEntity.builder()
        .properties(new HashMap<>())
        .build();
    feature.setDoubleProperty("2023-11-sentiment", 0.5);
    assertThat(feature.getDoubleProperty("2023-11-sentiment")).isEqualTo(0.5);
  }

  @Test
  void testGetDoublePropertyReturnsNullWithInvalidKey() {
    FeatureEntity feature = FeatureEntity.builder()
        .properties(new HashMap<>())
        .build();
    assertThat(feature.getDoubleProperty("2023-11-sentiment") == null).isTrue();
  }

}