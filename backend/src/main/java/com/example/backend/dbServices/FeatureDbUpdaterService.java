package com.example.backend.dbServices;

import com.example.backend.entity.ArticleEntity;
import com.example.backend.entity.FeatureEntity;
import com.example.backend.geocodingService.GeocodeResponse;
import com.example.backend.geocodingService.GeocodeResult;
import com.example.backend.geocodingService.GeocodingService;
import com.example.backend.nerService.ArticleNerProperties;
import com.example.backend.nerService.NerService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FeatureDbUpdaterService {

  private final FeatureDbService featureDbService;
  private final NerService nerService;
  private final GeocodingService geocodingService;

  public FeatureDbUpdaterService(
      FeatureDbService featureDbService,
      NerService nerService,
      GeocodingService geocodingService){
    this.featureDbService = featureDbService;
    this.nerService = nerService;
    this.geocodingService = geocodingService;
  }

  public ArticleNerProperties updateFeaturesForArticle(
      ArticleEntity article) throws Exception{
    String headline = article.getWebTitle();
    String bodyText = article.getBodyText();
    List<String> locations = nerService.getEntities(headline);
    if (locations.isEmpty()){
      return ArticleNerProperties.builder()
          .locations(new ArrayList<>())
          .numAssociatedFeatures(0)
          .sentimentScore(0.5)
          .build();
    }
    Thread.sleep(200); // for rate-limiting issues
    String concatText = headline.concat(bodyText);
    Double articleSentimentScore = nerService.getSentimentScore(concatText.length() >=512 ? headline.concat(bodyText).substring(0, 512) : concatText);
    LocalDateTime articleWebPublicationDate = article.getWebPublicationDate();
    String formattedFullDate = getFormattedDateString(articleWebPublicationDate);
    String formattedYearDate = formattedFullDate.substring(3, 7);

    int addedFeatures = 0;
    List<String> formattedLocations = new ArrayList<>();
    for (String location: locations){

      GeocodeResponse geocodeResponse = geocodingService.getGeocodeResultsForLocation(location);
      if (geocodeResponse == null) continue;
      List<GeocodeResult> results = geocodeResponse.getResults();
      if (results.isEmpty()) continue;

      GeocodeResult geocodeResult = results.get(0);
      String formattedLocation = geocodeResult.getFormatted_address();
      Double lat = geocodeResult.getGeometry().getLocation().get("lat");
      Double lng = geocodeResult.getGeometry().getLocation().get("lng");

      formattedLocations.add(formattedLocation);

      FeatureEntity feature = findOrCreateFeatureEntity(
          formattedLocation,
          formattedYearDate,
          formattedFullDate,
          lng,
          lat,
          articleSentimentScore
      );

      updateFeatureProperties(feature, formattedFullDate, formattedYearDate, articleSentimentScore);
      featureDbService.saveOne(feature);

      addedFeatures++;
    }
    return ArticleNerProperties.builder()
        .numAssociatedFeatures(addedFeatures)
        .sentimentScore(articleSentimentScore)
        .locations(formattedLocations)
        .build();
  }

  private FeatureEntity findOrCreateFeatureEntity(
      String formattedLocation,
      String formattedYearDate,
      String formattedFullDate,
      Double lng,
      Double lat,
      Double articleSentimentScore
      ) {
    Optional<FeatureEntity> existingFeature = featureDbService.findFeatureByLocation(formattedLocation);

    if (existingFeature.isEmpty()) {
      FeatureEntity feature = featureDbService.createFeatureEntity(lng, lat, formattedLocation);

      setCountSentimentProperties(feature, formattedYearDate, 1.0, articleSentimentScore);
      setCountSentimentProperties(feature, formattedFullDate, 1.0, articleSentimentScore);

      return feature;
    } else {
      return existingFeature.get();
    }
  }

  private void updateFeatureProperties(
      FeatureEntity feature,
      String formattedFullDate,
      String formattedYearDate,
      Double articleSentimentScore) {
    Double currentYearSentimentScoreAvg = feature.getDoubleProperty(formattedYearDate + "-sentiment");
    Double currentYearTotal = feature.getDoubleProperty(formattedYearDate + "-count");
    boolean yearSentimentPropertiesExist = currentYearSentimentScoreAvg != null && currentYearTotal != null;

    Double currentMonthSentimentScoreAvg = feature.getDoubleProperty(formattedFullDate + "-sentiment");
    Double currentMonthTotal = feature.getDoubleProperty(formattedFullDate + "-count");
    boolean monthSentimentPropertiesExist = currentMonthSentimentScoreAvg != null && currentMonthTotal != null;

    Double newMonthSentimentAvg = monthSentimentPropertiesExist
        ? ((currentMonthSentimentScoreAvg * currentMonthTotal) + articleSentimentScore) / (currentMonthTotal + 1)
        : articleSentimentScore;

    Double newYearSentimentAvg = yearSentimentPropertiesExist
        ? ((currentYearSentimentScoreAvg * currentYearTotal) + articleSentimentScore) / (currentYearTotal + 1)
        : articleSentimentScore;

    setCountSentimentProperties(feature, formattedFullDate, currentMonthTotal != null ? currentMonthTotal + 1 : 1, newMonthSentimentAvg);
    setCountSentimentProperties(feature, formattedYearDate, currentYearTotal != null ? currentYearTotal + 1 : 1, newYearSentimentAvg);

  }

  private void setCountSentimentProperties(
      FeatureEntity feature,
      String date,
      Double count,
      Double sentimentScore){
    feature.setDoubleProperty(date+"-count", count);
    feature.setDoubleProperty(date+"-sentiment", sentimentScore);
  }

  private String getFormattedDateString(LocalDateTime articleWebPublicationDate){
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
    return articleWebPublicationDate.format(formatter);
  }

}
