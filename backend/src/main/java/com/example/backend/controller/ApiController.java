package com.example.backend.controller;

import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.dbServices.FeatureDbService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.guardianService.GuardianService;
import com.example.backend.guardianService.responseRelated.AugmentedContentResponse;
import com.example.backend.mapboxGeocodingService.MapboxGeocodingService;
import com.example.backend.processor.Processor;
import com.example.backend.response.RestApiResponse;
import com.example.backend.response.RestApiSuccessResponse;
import com.example.backend.response.ResponseCode;
import com.example.backend.response.RestApiFailureResponse;

import com.example.backend.sentimentAnalysisService.SentimentAnalysisService;
import java.io.FileNotFoundException;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import the.guardian.api.http.content.ContentResponse;
import the.guardian.api.http.editions.EditionsResponse;

import java.security.Guard;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;


@RestController
@Slf4j
@RequestMapping("/api")
public class ApiController {

    private final GuardianService guardianService;
    private final ArticleDbService articleDbService;

    private final FeatureDbService featureDbService;

    private final SentimentAnalysisService sentimentAnalysisService;

    private final MapboxGeocodingService mapboxGeocodingService;

    @Autowired
    public ApiController(GuardianService guardianService,
                         ArticleDbService articleDbService,
         SentimentAnalysisService sentimentAnalysisService,
         MapboxGeocodingService mapboxGeocodingService,
          FeatureDbService featureDbService) {
        this.guardianService = guardianService;
        this.articleDbService = articleDbService;
        this.sentimentAnalysisService = sentimentAnalysisService;
        this.mapboxGeocodingService = mapboxGeocodingService;
        this.featureDbService = featureDbService;
    }

    @GetMapping("/search")
    public RestApiResponse handleSearch(
        @RequestParam(required = true) String input,
        @RequestParam(required = false) String fromDate,
        @RequestParam(required = false) String toDate){
        try {
            List<ArticleEntity> articles = articleDbService.searchByInput(input, Optional.of(fromDate), Optional.of(toDate));
            return new RestApiSuccessResponse<List<ArticleEntity>>(articles);
        } catch (Exception e){
            return new RestApiFailureResponse(400, e.getMessage()); 
        }
    }

    @GetMapping("/searchByLocation")
    public RestApiResponse handleSearchByLocation(
        @RequestParam(required = true) String location, 
        @RequestParam(required = false) String fromDate,
        @RequestParam(required = false) String toDate){
        try {
            List<ArticleEntity> articles = articleDbService.searchByLocation(location, Optional.of(fromDate), Optional.of(toDate));
            return new RestApiSuccessResponse<List<ArticleEntity>>(articles);
        } catch (Exception e){
            return new RestApiFailureResponse(400, e.getMessage()); 
        }
   }

    @GetMapping("/searchByDateRange")
    public RestApiResponse handleSearchByDateRange(
        @RequestParam(required = true) String fromDate, 
        @RequestParam(required =  true) String toDate){
        try {
            List<ArticleEntity> articles = articleDbService.searchByDateRange(fromDate, toDate);
            return new RestApiSuccessResponse<List<ArticleEntity>>(articles);
        } catch (Exception e){
            return new RestApiFailureResponse(400, e.getMessage()); 
        }
    }

    @GetMapping("/getArticleContent")
    public RestApiResponse handleGetGuardianResponse(
        @RequestParam(required = true) String fromDate, 
        @RequestParam(required =  true) String toDate){
        try {
            ContentResponse response = guardianService.fetchArticlesByDateRange(fromDate, toDate);
            if (response == null || !response.getStatus().equals("ok")){
                return new RestApiFailureResponse(500, "Error retrieving articles from the Guardian API");
            }
            return new RestApiSuccessResponse<ContentResponse>(response);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return new RestApiFailureResponse(500, e.getMessage());
        }
    }

    @GetMapping("/processArticles")
    public void handleProcess(){
         try {
           Processor processor = new Processor(
               guardianService,
               articleDbService,
               sentimentAnalysisService,
               mapboxGeocodingService,
               featureDbService);
           processor.processArticles("2023-11-20", "2023-11-20");
         } catch (IOException e){
           System.out.println("Error initializing the processor: " + e.getMessage());
         }

        }
}
