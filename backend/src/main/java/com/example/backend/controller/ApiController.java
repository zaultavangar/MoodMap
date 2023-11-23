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

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import the.guardian.api.http.content.ContentResponse;
import the.guardian.api.http.editions.EditionsResponse;

import java.security.Guard;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;


@RestController
@EnableCaching
@Slf4j
@RequestMapping("/api")
public class ApiController {

    private RabbitTemplate rabbitTemplate;
    private final GuardianService guardianService;
    private final ArticleDbService articleDbService;
    private final FeatureDbService featureDbService;

    private final SentimentAnalysisService sentimentAnalysisService;

    private final MapboxGeocodingService mapboxGeocodingService;

    private final Processor processor;

    @Autowired
    public ApiController(Processor processor,
        GuardianService guardianService,
        ArticleDbService articleDbService,
         SentimentAnalysisService sentimentAnalysisService,
         MapboxGeocodingService mapboxGeocodingService,
          FeatureDbService featureDbService,
          RabbitTemplate rabbitTemplate) {
        this.guardianService = guardianService;
        this.articleDbService = articleDbService;
        this.sentimentAnalysisService = sentimentAnalysisService;
        this.mapboxGeocodingService = mapboxGeocodingService;
        this.featureDbService = featureDbService;
        this.rabbitTemplate = rabbitTemplate;
        this.processor = processor;
    }

    @GetMapping("/search")
    public RestApiResponse handleSearch(
        @RequestParam(required = true) String input,
        @RequestParam(required = false) String fromDate,
        @RequestParam(required = false) String toDate){
        try {
            // TODO: Change to handle case with and without dates
            if (StringUtils.isEmpty(fromDate) || StringUtils.isEmpty(toDate)) {
                List<ArticleEntity> articles = articleDbService.searchByInput(input);
                return new RestApiSuccessResponse<>(articles);
            }
            List<ArticleEntity> articleEntityList = articleDbService.searchByInput(input, fromDate, toDate);
            return new RestApiSuccessResponse<>(articleEntityList);
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
            List<ArticleEntity> articles = new ArrayList<>();
            if (StringUtils.isEmpty(fromDate) || StringUtils.isEmpty(toDate)) {
                articles = articleDbService.searchByLocation(location);
                return new RestApiSuccessResponse<>(articles);
            }
            articles = articleDbService.searchByLocation(location, fromDate, toDate);
            return new RestApiSuccessResponse<>(articles);
        } catch (Exception e){
            return new RestApiFailureResponse(400, e.getMessage()); 
        }
   }

    @GetMapping("/searchByDateRange")
    public RestApiResponse handleSearchByDateRange(
        @RequestParam(required = true) String fromDate, 
        @RequestParam(required =  true) String toDate){
        try {
            // TODO: Change to handle case with and without dates
            List<ArticleEntity> articles = articleDbService.searchByDateRange(fromDate, toDate);
            return new RestApiSuccessResponse<List<ArticleEntity>>(articles);
        } catch (Exception e){
            return new RestApiFailureResponse(400, e.getMessage()); 
        }
    }


    @GetMapping("/processArticles")
    public void handleProcess(){
      processor.processArticles("2023-11-18", "2023-11-19", false);

    }

}
