package com.example.backend.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FeatureEntityTest {

  /**
   * Tests if the setPropertiesLocation method successfully adds a location
   * to the properties of a FeatureEntity. This test ensures that after setting
   * the location property, it can be retrieved correctly.
   */
  @Test
  void testSetPropertiesLocationSuccessfullyAddsLocation() {
    FeatureEntity feature = FeatureEntity.builder()
        .properties(new HashMap<>())
        .build();
    feature.setPropertiesLocation("China");
    assertThat(feature.getProperties().get("location")).isEqualTo("China");
  }


  /**
   * Tests if the setDoubleProperty method correctly updates the properties of
   * a FeatureEntity with a double value. This test checks that after setting
   * a double property, it can be retrieved accurately.
   */
  @Test
  void testSetDoublePropertySuccessfullyUpdatesProperties() {
    FeatureEntity feature = FeatureEntity.builder()
        .properties(new HashMap<>())
        .build();
    feature.setDoubleProperty("2023-11-sentiment", 0.5);
    assertThat(feature.getDoubleProperty("2023-11-sentiment")).isEqualTo(0.5);
  }


  /**
   * Tests the behavior of getDoubleProperty method when the key is invalid.
   * This test ensures that the method returns null if a non-existent key is queried,
   * which is expected behavior for the retrieval of an undefined property.
   */
  @Test
  void testGetDoublePropertyReturnsNullWithInvalidKey() {
    FeatureEntity feature = FeatureEntity.builder()
        .properties(new HashMap<>())
        .build();
    assertThat(feature.getDoubleProperty("2023-11-sentiment") == null).isTrue();
  }

}