package com.example.backend.controller;

import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.guardianService.GuardianService;
import com.example.backend.response.RestApiResponse;
import com.example.backend.response.RestApiSuccessResponse;
import com.example.backend.response.ResponseCode;
import com.example.backend.response.RestApiFailureResponse;

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

    @Autowired
    public ApiController(GuardianService guardianService,
                         ArticleDbService articleDbService) {
        this.guardianService = guardianService;
        this.articleDbService = articleDbService;
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


    // @GetMapping("/test")
    // public RestApiResponse test(@RequestParam @NonNull String editions) {
    //     EditionsResponse editionsResponse = guardianService.fetchByEdition(editions);
    //     if (editionsResponse != null && editionsResponse.getStatus().equals("ok")) {
    //         RestApiResponse response = RestApiResponse.successResponse();
    //         if (editionsResponse.getTotal() != 0) {
    //             response.setData(editionsResponse.getResults());
    //         }
    //         return response;
    //     }
    //     RestApiResponse response = RestApiResponse.failResponse(1001, "empty list");
    //     return response;
    // }

    // @GetMapping("/getContent")
    // public RestApiResponse getContent(@RequestParam @DefaultValue("") @Nullable String keyWord,
    //                                   @RequestParam @DefaultValue("") @Nullable String tag,
    //                                   @RequestParam @DefaultValue("") @Nullable String fromDate) {
    //     ContentResponse contentResponse = guardianService.fetchByContent(keyWord, tag, fromDate);
    //     if (contentResponse != null && contentResponse.getStatus().equals("ok")) {
    //         RestApiResponse response = RestApiResponse.successResponse();
    //         if (contentResponse.getTotal() != 0) {
    //             response.setData(contentResponse.getResults());
    //         }
    //         return response;
    //     }
    //     RestApiResponse response = RestApiResponse.failResponse(
    //             ResponseCode.ERROR_CALLING_GUARDIAN_CONTENT_API.getCode(),
    //             ResponseCode.ERROR_CALLING_GUARDIAN_CONTENT_API.getErrorMessage());
    //     return response;
    // }

    // @GetMapping("/baidu")
    // public void testout() {
    //     guardianService.testInsert("world","","2023-11-20");
    // }
}
