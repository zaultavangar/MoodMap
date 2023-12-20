package com.example.backend.controller;

import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.dbServices.FeatureDbService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.entity.FeatureDTO;
import com.example.backend.processors.Processor;
import com.example.backend.response.ArticleEntityListApiResponse;
import com.example.backend.response.RestApiResponse;
import com.example.backend.response.RestApiSuccessResponse;
import com.example.backend.response.RestApiFailureResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import com.example.backend.validator.RequestValidator;
import com.example.backend.validator.SearchRequest;
import com.example.backend.validator.ValidationResult;

/**
 * ApiController is a REST controller that provides API endpoints for managing and querying
 * articles and features. It supports operations like searching for articles based on input phrases,
 * locations, and date ranges,as well as retrieving features (locations) from the database.
 */
@RestController
@EnableCaching
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class ApiController {

    private final ArticleDbService articleDbService;
    private final FeatureDbService featureDbService;
    private final Processor processor;

  /**
   * Constructs a new ApiController with specified services and processor.
   *
   * @param processor The processor for article processing.
   * @param articleDbService The database service for articles.
   * @param featureDbService The database service for features.
   */
    public ApiController(
        Processor processor,
        ArticleDbService articleDbService,
        FeatureDbService featureDbService
    ) {
        this.articleDbService = articleDbService;
        this.processor = processor;
        this.featureDbService = featureDbService;
    }

  /**
   * Creates a complete search request with optional parameters.
   *
   * @param input The input phrase for search.
   * @param fromDate The start date of the search range.
   * @param toDate The end date of the search range.
   * @return A SearchRequest object populated with the provided parameters.
   */
    private SearchRequest createCompleteSearchRequest(String input, String fromDate, String toDate){
        return new SearchRequest(
            Optional.ofNullable(input),
            Optional.ofNullable(fromDate),
            Optional.ofNullable(toDate));
    }

  /**
   * Checks if both fromDate and toDate are present in the search request.
   *
   * @param searchRequest The search request containing date parameters.
   * @return ValidationResult indicating whether both dates are present, not present, or inconsistent.
   */
    private ValidationResult getDatesPresent(SearchRequest searchRequest){
        return RequestValidator.areDatesPresent()
            .apply(searchRequest);
    }

  /**
   * Validates the search request based on the requirements for input and date range.
   *
   * @param searchRequest The search request to be validated.
   * @param inputRequired Indicates if input is a required parameter.
   * @param datesRequired Indicates if dates are required parameters.
   * @return ValidationResult indicating the result of the validation (success or type of failure).
   */
    private ValidationResult validateSearchRequest(
        SearchRequest searchRequest,
        boolean inputRequired,
        boolean datesRequired) {
        return RequestValidator.isInputValid(inputRequired)
            .and(RequestValidator.isDateRangeValid(datesRequired))
            .apply(searchRequest);
    }

  /**
   * Retrieves the list of features from the database.
   *
   * @return A RestApiResponse containing a list of FeatureDTOs if successful, or an error message if an exception occurs.
   */
    @Operation(summary = "To retrieve the features, aka locations, in the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of features",
            content = @Content(mediaType = "application/json", schema = @Schema(name = "SuccessResponse", implementation = ArticleEntityListApiResponse.class))),
    })
    @GetMapping("/getFeatures")
    public RestApiResponse<Object> getFeatures(){
      try {
        List<FeatureDTO> features = featureDbService.getFeatures();
        return new RestApiSuccessResponse<>(features);
      } catch (Exception e){
        return new RestApiFailureResponse(500, e.getMessage());
      }
    }

  /**
   * Searches for articles based on a given input phrase and optional date range.
   *
   * @param input The input phrase for the search.
   * @param fromDate The start date of the search range (optional).
   * @param toDate The end date of the search range (optional).
   * @return A RestApiResponse containing a list of ArticleEntities if successful, or an error message if validation fails or an exception occurs.
   */
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

  /**
   * Searches for articles based on a given location and optional date range.
   *
   * @param location The location to search articles for.
   * @param fromDate The start date of the search range (optional).
   * @param toDate The end date of the search range (optional).
   * @return A RestApiResponse containing a list of ArticleEntities if successful, or an error message if validation fails or an exception occurs.
   */
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
            return new RestApiFailureResponse(400, requestResult.getMessage());
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

  /**
   * Searches for articles within a specified date range.
   *
   * @param fromDate The start date of the search range (required).
   * @param toDate The end date of the search range (required).
   * @return A RestApiResponse containing a list of ArticleEntities if successful, or an error message if validation fails or an exception occurs.
   */
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

        System.out.println(requestResult);
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


  /**
   * Processes articles for a given date range. Note: this endpoint is only for our own processing
   * purposes, and is not to be used by the frontend.
   *
   * @throws Exception if there is an error during processing.
   */
    @GetMapping("/processArticles")
    public void handleProcess() throws Exception{
      processor.processArticles("2023-12-19", "2023-12-20", true);

    }

}
