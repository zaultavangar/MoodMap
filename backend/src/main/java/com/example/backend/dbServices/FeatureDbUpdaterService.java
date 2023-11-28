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
    List<String> locations = nerService.getEntities(headline);
    if (locations.isEmpty()){
      return ArticleNerProperties.builder()
          .locations(new ArrayList<>())
          .numAssociatedFeatures(0)
          .sentimentScore(null)
          .build();
    }
    Double articleSentimentScore = nerService.getSentimentScore(headline);
    LocalDateTime articleWebPublicationDate = article.getWebPublicationDate();
    String formattedDate = getFormattedDateString(articleWebPublicationDate);

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
      FeatureEntity feature = featureDbService.createFeatureEntity(lng, lat, formattedLocation);

      if (feature == null) continue;
      Optional<FeatureEntity> existingFeature = featureDbService.findFeatureByLocation(feature.getLocation());
      if (existingFeature.isEmpty()){ // check if feature already exists in DB
        feature.setDoubleProperty(formattedDate + "-count", 1.0); // set count of articles to 1
        feature.setDoubleProperty(formattedDate + "-sentiment", articleSentimentScore); // new avg sentiment
        featureDbService.saveOne(feature);
      } else { // update article sentiment properties
        // check if sentiment score for that month-year exists
        Double currentSentimentScoreAvg = existingFeature.get().getDoubleProperty(formattedDate+"-sentiment");
        Double currentTotal = existingFeature.get().getDoubleProperty(formattedDate+"-count");
        boolean sentimentPropertiesExist = currentSentimentScoreAvg != null && currentTotal != null;

        Double newSentimentAvg = sentimentPropertiesExist ?
            ((currentSentimentScoreAvg*currentTotal)+articleSentimentScore)/(currentTotal+1)
            : articleSentimentScore;

        feature.setDoubleProperty(formattedDate+"-sentiment", newSentimentAvg);
        feature.setDoubleProperty(formattedDate+"-count", currentTotal !=null ? currentTotal+1 : 1);
        feature.set_id(existingFeature.get().get_id());
        featureDbService.saveOne(feature);
      }
      addedFeatures++;
    }
    return ArticleNerProperties.builder()
        .numAssociatedFeatures(addedFeatures)
        .sentimentScore(articleSentimentScore)
        .locations(formattedLocations)
        .build();
  }

  private String getFormattedDateString(LocalDateTime articleWebPublicationDate){
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
    return articleWebPublicationDate.format(formatter);
  }

}
