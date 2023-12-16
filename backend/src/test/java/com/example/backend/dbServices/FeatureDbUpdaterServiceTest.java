package com.example.backend.dbServices;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.backend.entity.ArticleEntity;
import com.example.backend.entity.FeatureEntity;
import com.example.backend.geocodingService.GeoJsonGeometry;
import com.example.backend.geocodingService.GeocodeGeometry;
import com.example.backend.geocodingService.GeocodeResponse;
import com.example.backend.geocodingService.GeocodeResult;
import com.example.backend.geocodingService.GeocodingService;
import com.example.backend.nerService.ArticleNerProperties;
import com.example.backend.nerService.NerService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FeatureDbUpdaterServiceTest {

  @Mock
  private FeatureDbService featureDbService;

  @Mock
  private NerService nerService;

  @Mock
  private GeocodingService geocodingService;

  @InjectMocks
  private FeatureDbUpdaterService featureDbUpdaterService;

  @Test
  void testUpdateFeaturesForArticle(){
    try {
      String headline = "Brazil fights Argentina";
      String bodyText = "Article body text";

      ArticleEntity article = ArticleEntity.builder()
          ._id(new ObjectId())
          .webPublicationDate(LocalDateTime.now())
          .webTitle("Brazil fights Argentina")
          .webUrl("www.article.com")
          .thumbnail("www.thumb-nail.com")
          .bodyText("Article body text")
          .sentimentScore(0.5)
          .associatedLocations(new ArrayList<>())
          .build();

        List<String> locations = List.of("Brazil", "Argentina");
        when(nerService.getEntities(headline))
            .thenReturn(locations);

        when(nerService.getSentimentScore(any(String.class)))
            .thenReturn(0.2);

        Map<String, Double> map1 = Map.of("lat", -40d, "lng", 70d);
        Map<String, Double> map2 = Map.of("lat", 0d, "lng", 0d);
        GeocodeGeometry geocodeGeometry1 = new GeocodeGeometry(map1);
        GeocodeGeometry geocodeGeometry2 = new GeocodeGeometry(map2);
        GeocodeResult geocodeResult1 = new GeocodeResult(
            "Brazil", geocodeGeometry1
        );
        GeocodeResult geocodeResult2 = new GeocodeResult(
            "Argentina", geocodeGeometry2
        );

        GeocodeResponse geocodeResponse1 = new GeocodeResponse(List.of(geocodeResult1));
        GeocodeResponse geocodeResponse2 = new GeocodeResponse((List.of(geocodeResult2)));

        when(geocodingService.getGeocodeResultsForLocation(any(String.class)))
            .thenReturn(geocodeResponse1)
            .thenReturn(geocodeResponse2);

        when(featureDbService.findFeatureByLocation(any(String.class)))
            .thenReturn(Optional.empty())
            .thenReturn(Optional.empty());

      GeoJsonGeometry geoJsonGeometry1 = GeoJsonGeometry.builder()
          .type("Point")
          .coordinates(List.of(-40d, 70d))
          .build();
      GeoJsonGeometry geoJsonGeometry2 = GeoJsonGeometry.builder()
          .type("Point")
          .coordinates(List.of(0d, 0d))
          .build();
        FeatureEntity feature1 = FeatureEntity.builder()
            ._id(new ObjectId())
            .type("Feature")
            .location("Brazil")
            .properties(new HashMap<>())
            .geoJsonGeometry(geoJsonGeometry1)
            .build();
      FeatureEntity feature2 = FeatureEntity.builder()
          ._id(new ObjectId())
          .type("Feature")
          .location("Argentina")
          .properties(new HashMap<>())
          .geoJsonGeometry(geoJsonGeometry2)
          .build();



        when(featureDbService.createFeatureEntity(any(Double.class), any(Double.class), any(String.class)))
            .thenReturn(feature1)
            .thenReturn(feature2);

        doNothing().when(featureDbService).saveOne(any(FeatureEntity.class));

        ArticleNerProperties articleNerProperties = featureDbUpdaterService.updateFeaturesForArticle(
            article);

        assertEquals(2, articleNerProperties.numAssociatedFeatures());
        assertEquals(0.2, articleNerProperties.sentimentScore());
        assertEquals(List.of("Brazil", "Argentina"), articleNerProperties.locations());
    } catch (Exception e){
      fail();
    }

  }

  @Test
  void testUpdateFeaturesForArticleWithNoLocationsFound(){
    try {
      String headline = "Brazil fights Argentina";
      String bodyText = "Article body text";

      ArticleEntity article = ArticleEntity.builder()
          ._id(new ObjectId())
          .webPublicationDate(LocalDateTime.now())
          .webTitle(headline)
          .webUrl("www.article.com")
          .thumbnail("www.thumb-nail.com")
          .bodyText(bodyText)
          .sentimentScore(0.5)
          .associatedLocations(new ArrayList<>())
          .build();

      when(nerService.getEntities(headline))
          .thenReturn(new ArrayList<>());


      ArticleNerProperties articleNerProperties = featureDbUpdaterService.updateFeaturesForArticle(
          article);

      verifyNoInteractions(geocodingService);
      verifyNoInteractions(featureDbService);

      assertEquals(0, articleNerProperties.numAssociatedFeatures());
      assertEquals(0.5, articleNerProperties.sentimentScore());
      assertEquals(new ArrayList<>(), articleNerProperties.locations());
    } catch (Exception e){
      fail();
    }

  }

  @Test
  void testUpdateFeaturesForArticleWithNullGeocodeResponse(){
    try {
      String headline = "Brazil fights Argentina";
      String bodyText = "Article body text";

      ArticleEntity article = ArticleEntity.builder()
          ._id(new ObjectId())
          .webPublicationDate(LocalDateTime.now())
          .webTitle("Brazil fights Argentina")
          .webUrl("www.article.com")
          .thumbnail("www.thumb-nail.com")
          .bodyText("Article body text")
          .sentimentScore(0.5)
          .associatedLocations(new ArrayList<>())
          .build();

      List<String> locations = List.of("Brazil", "Argentina");
      when(nerService.getEntities(headline))
          .thenReturn(locations);

      when(nerService.getSentimentScore(any(String.class)))
          .thenReturn(0.2);

      Map<String, Double> map1 = Map.of("lat", -40d, "lng", 70d);
      GeocodeGeometry geocodeGeometry1 = new GeocodeGeometry(map1);
      GeocodeResult geocodeResult1 = new GeocodeResult(
          "Brazil", geocodeGeometry1
      );


      GeocodeResponse geocodeResponse1 = new GeocodeResponse(List.of(geocodeResult1));

      when(geocodingService.getGeocodeResultsForLocation(any(String.class)))
          .thenReturn(geocodeResponse1)
          .thenReturn(null);

      GeoJsonGeometry geoJsonGeometry = GeoJsonGeometry.builder()
          .type("Point")
          .coordinates(List.of(-40d, 70d))
          .build();
      FeatureEntity feature = FeatureEntity.builder()
          ._id(new ObjectId())
          .type("Feature")
          .location("Brazil")
          .properties(new HashMap<>())
          .geoJsonGeometry(geoJsonGeometry)
          .build();




      when(featureDbService.createFeatureEntity(any(Double.class), any(Double.class), any(String.class)))
          .thenReturn(feature);

      doNothing().when(featureDbService).saveOne(any(FeatureEntity.class));

      ArticleNerProperties articleNerProperties = featureDbUpdaterService.updateFeaturesForArticle(
          article);

      assertEquals(1, articleNerProperties.numAssociatedFeatures());
      assertEquals(0.2, articleNerProperties.sentimentScore());
      assertEquals(List.of("Brazil"), articleNerProperties.locations());
    } catch (Exception e){
      System.out.println(e);
      fail();
    }

  }
}
