package com.example.backend.controller;

import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.dbServices.FeatureDbService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.entity.FeatureEntity;
import com.example.backend.entity.FeatureProjection;
import com.example.backend.processors.DailyProcessor;
import com.example.backend.response.ArticleEntityListApiResponse;
import com.example.backend.response.RestApiResponse;
import com.example.backend.response.RestApiSuccessResponse;
import com.example.backend.response.RestApiFailureResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

import org.checkerframework.checker.units.qual.A;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import com.example.backend.validator.RequestValidator;
import com.example.backend.validator.SearchRequest;
import com.example.backend.validator.ValidationResult;


@RestController
@EnableCaching
@Slf4j
@RequestMapping("/api")
public class ApiController {

    private final ArticleDbService articleDbService;

    private final FeatureDbService featureDbService;
    private final DailyProcessor dailyProcessor;

    public ApiController(
        DailyProcessor dailyProcessor,
        ArticleDbService articleDbService,
        FeatureDbService featureDbService) {
        this.articleDbService = articleDbService;
        this.dailyProcessor = dailyProcessor;
        this.featureDbService = featureDbService;
    }

    private SearchRequest createCompleteSearchRequest(String input, String fromDate, String toDate){
        return new SearchRequest(
            Optional.ofNullable(input),
            Optional.ofNullable(fromDate),
            Optional.ofNullable(toDate));
    }

    private ValidationResult getDatesPresent(SearchRequest searchRequest){
        return RequestValidator.areDatesPresent()
            .apply(searchRequest);
    }

    private ValidationResult validateSearchRequest(
        SearchRequest searchRequest,
        boolean inputRequired,
        boolean datesRequired) {
        return RequestValidator.isInputValid(inputRequired)
            .and(RequestValidator.isFromDateValid(datesRequired))
            .and(RequestValidator.isToDateValid(datesRequired))
            .apply(searchRequest);
    }




    @Operation(summary = "To retrieve the features, aka locations, in the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of features",
            content = @Content(mediaType = "application/json", schema = @Schema(name = "SuccessResponse", implementation = ArticleEntityListApiResponse.class))),
    })
    @GetMapping("/getFeatures")
    public RestApiResponse<Object> getFeatures(){
      try {
        List<FeatureProjection> features = featureDbService.getFeatures();
        return new RestApiSuccessResponse<>(features);
      } catch (Exception e){
        return new RestApiFailureResponse(500, e.getMessage());
      }


    }

    @Operation(summary = "To search for articles based on some input phrase. Can narrow down using a date range as well.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of articles",
            content = @Content(mediaType = "application/json", schema = @Schema(name = "SuccessResponse", implementation = ArticleEntityListApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json" , schema = @Schema(name = "FailureResponse",implementation = RestApiFailureResponse.class)))
    })
    @GetMapping("/search")
    public RestApiResponse<Object> handleSearch(
        @RequestParam(required = true) String input,
        @RequestParam(required = false) String fromDate,
        @RequestParam(required = false) String toDate){

        SearchRequest searchRequest = createCompleteSearchRequest(input, fromDate, toDate);
        ValidationResult datesPresentResult = getDatesPresent(searchRequest);
        ValidationResult requestResult = validateSearchRequest(searchRequest, true, false);

        if (!requestResult.equals(ValidationResult.SUCCESS)) {
            return new RestApiFailureResponse(400, requestResult.getMessage());
        }

        if (datesPresentResult.equals(ValidationResult.DATES_INCONSISTENT)) {
            return new RestApiFailureResponse(400, "Both fromDate and toDate must be provided or omitted together");
        }

        try {
            List<ArticleEntity> articles =
                datesPresentResult.equals((ValidationResult.DATES_PRESENT))
                    ? articleDbService.searchByInput(input, fromDate, toDate)
                    : articleDbService.searchByInput(input);
            return new RestApiSuccessResponse<>(articles);

        } catch (Exception e) {
            return new RestApiFailureResponse(500, e.getMessage());
        }
    }

    @Operation(summary = "To search for articles based on a location (e.g. 'France'). Can narrow down using a date range as well.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of articles",
            content = @Content(mediaType = "application/json", schema = @Schema(name = "SuccessResponse", implementation = ArticleEntityListApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", schema = @Schema(name = "FailureResponse", implementation = RestApiFailureResponse.class)))
    })
    @GetMapping("/searchByLocation")
    public RestApiResponse<Object> handleSearchByLocation(
        @RequestParam(required = true) String location,
        @RequestParam(required = false) String fromDate,
        @RequestParam(required = false) String toDate){

        SearchRequest searchRequest = createCompleteSearchRequest(location, fromDate, toDate);
        ValidationResult datesPresentResult = getDatesPresent(searchRequest);
        ValidationResult requestResult = validateSearchRequest(searchRequest, true, false);

        if (!requestResult.equals(ValidationResult.SUCCESS)) {
            return new RestApiResponse<>(400, requestResult.getMessage(),null);
        }

        if (datesPresentResult.equals(ValidationResult.DATES_INCONSISTENT)) {
            return new RestApiFailureResponse(400, "Both fromDate and toDate must be provided or omitted together");
        }

        try {
            List<ArticleEntity> articles =
                datesPresentResult.equals((ValidationResult.DATES_PRESENT))
                    ? articleDbService.searchByLocation(location, fromDate, toDate)
                    : articleDbService.searchByLocation(location);
            return new RestApiSuccessResponse<>(articles);

        } catch (Exception e) {
            return new RestApiFailureResponse(500,e.getMessage());
        }
   }

    @Operation(summary = "To search for articles based on a specified date range. Query params fromDate and toDate must "
        + "be in the following format: yyyy-mm-dd (e.g. 2023-11-20).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of articles",
            content = @Content(mediaType = "application/json", schema = @Schema(name = "SuccessResponse", implementation = ArticleEntityListApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json" , schema = @Schema(name = "FailureResponse", implementation = RestApiFailureResponse.class)))
    })
    @GetMapping("/searchByDateRange")
   public RestApiResponse<Object> handleSearchByDateRange(
        @RequestParam(required = true) String fromDate,
        @RequestParam(required = true) String toDate){

        SearchRequest searchRequest = createCompleteSearchRequest(null, fromDate, toDate);
        ValidationResult requestResult = validateSearchRequest(searchRequest, false, true);

        if (!requestResult.equals(ValidationResult.SUCCESS)) {
            return new RestApiFailureResponse(400, requestResult.getMessage());
        }
        try {
            List<ArticleEntity> articles = articleDbService.searchByDateRange(fromDate, toDate);
            return new RestApiSuccessResponse<>(articles);

        } catch (Exception e) {
            return new RestApiFailureResponse(500,e.getMessage());
        }
    }


    @GetMapping("/processArticles")
    public void handleProcess(){
      dailyProcessor.processArticles("2022-01-01", "2022-05-31", true);

    }

}
