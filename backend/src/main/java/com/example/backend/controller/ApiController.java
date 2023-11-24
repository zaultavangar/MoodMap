package com.example.backend.controller;

import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.processor.Processor;
import com.example.backend.response.RestApiResponse;
import com.example.backend.response.RestApiSuccessResponse;
import com.example.backend.response.RestApiFailureResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import validator.RequestValidator;
import validator.SearchRequest;
import validator.ValidationResult;


@RestController
@EnableCaching
@Slf4j
@RequestMapping("/api")
public class ApiController {

    private final ArticleDbService articleDbService;
    private final Processor processor;

    @Autowired
    public ApiController(
        Processor processor,
        ArticleDbService articleDbService) {
        this.articleDbService = articleDbService;
        this.processor = processor;
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


    @GetMapping("/search")
    public RestApiResponse<List<ArticleEntity>> handleSearch(
        @RequestParam(required = true) String input,
        @RequestParam(required = false) String fromDate,
        @RequestParam(required = false) String toDate){

        SearchRequest searchRequest = createCompleteSearchRequest(input, fromDate, toDate);
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
                    ? articleDbService.searchByInput(input, fromDate, toDate)
                    : articleDbService.searchByInput(input);
            return new RestApiSuccessResponse<>(articles);

        } catch (Exception e) {
            return new RestApiFailureResponse(500, e.getMessage());
        }
    }


    @GetMapping("/searchByLocation")
    public RestApiResponse handleSearchByLocation(
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
                    : articleDbService.searchByLocation(location);;
            return new RestApiSuccessResponse<>(articles);

        } catch (Exception e) {
            return new RestApiFailureResponse(500,e.getMessage());
        }
   }

    @GetMapping("/searchByDateRange")
    public RestApiResponse handleSearchByDateRange(
        @RequestParam(required = true) String fromDate,
        @RequestParam(required = true) String toDate){

        SearchRequest searchRequest = createCompleteSearchRequest(null, fromDate, toDate);
        ValidationResult requestResult = validateSearchRequest(searchRequest, false, true);

        if (!requestResult.equals(ValidationResult.SUCCESS)) {
            return new RestApiResponse<>(400, requestResult.getMessage(),null);
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
      processor.processArticles("2023-11-18", "2023-11-19", true);

    }

}
