package com.example.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.dbServices.FeatureDbService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.entity.FeatureDTO;
import com.example.backend.response.RestApiFailureResponse;
import com.example.backend.response.RestApiResponse;
import com.example.backend.response.RestApiSuccessResponse;
import com.mongodb.MongoException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.UncategorizedMongoDbException;

// UNIT TESTS FOR REST CONTROLLER

//@AutoConfigureMockMvc(addFilters = false) // disable security, don't have to worry about adding token
@ExtendWith(MockitoExtension.class)
class ApiControllerUnitTest {

  @InjectMocks
  private ApiController apiController;

  @Mock
  private ArticleDbService articleDbService;

  @Mock
  private FeatureDbService featureDbService;

  @Test
  void testGetFeaturesSuccess() {
    List<FeatureDTO> mockFeatures = Arrays.asList(
        new FeatureDTO(), new FeatureDTO()
    );
    when(featureDbService.getFeatures()).thenReturn(mockFeatures);

    RestApiResponse<Object> response = apiController.getFeatures();

    assertTrue(response instanceof RestApiSuccessResponse<Object>);
    assertEquals(mockFeatures, response.getData());
    verify(featureDbService).getFeatures();
  }

  @Test
  void testGetFeaturesServerError() {
    when(featureDbService.getFeatures()).thenThrow(UncategorizedMongoDbException.class);

    RestApiResponse<Object> response = apiController.getFeatures();

    assertTrue(response instanceof RestApiFailureResponse);
    verify(featureDbService).getFeatures();
  }

  @Test
  void testHandleSearchWithOnlyInputSuccess() {
    List<ArticleEntity> mockArticles = Arrays.asList(
        new ArticleEntity(), new ArticleEntity()
    );

    when(articleDbService.searchByInput("gaza")).thenReturn(mockArticles);

    RestApiResponse<Object> response = apiController.handleSearch(
        "gaza",
        null,
        null);

    assertTrue(response instanceof RestApiSuccessResponse);
    assertEquals(mockArticles, response.getData());
    verify(articleDbService).searchByInput("gaza");
  }

  @Test
  void testHandleSearchWithInputAndDateRangeSuccess() {
    List<ArticleEntity> mockArticles = Arrays.asList(
        new ArticleEntity(), new ArticleEntity(), new ArticleEntity()
    );

    when(articleDbService.searchByInput(
        "gaza", "2023-11-01", "2023-11-20"
        )).thenReturn(mockArticles);

    RestApiResponse<Object> response = apiController.handleSearch(
        "gaza",
        "2023-11-01",
        "2023-11-20");

    assertTrue(response instanceof RestApiSuccessResponse);
    assertEquals(mockArticles, response.getData());
    verify(articleDbService).searchByInput("gaza", "2023-11-01", "2023-11-20");
  }

  @Test
  void testHandleSearchWithNoRequestParamsFailure() {
    RestApiResponse<Object> response = apiController.handleSearch(null,null,null);
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  @Test
  void testHandleSearchWithEmptyInputFailure() {
    RestApiResponse<Object> response = apiController.handleSearch(" ",null,null);
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  @Test
  void testHandleSearchWithInvalidToDateParam() {
    RestApiResponse<Object> response = apiController.handleSearch(
        "gaza",
        "2022-01-01",
        "2022-03");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  @Test
  void testHandleSearchWithInvalidFromDateParam() {
    RestApiResponse<Object> response = apiController.handleSearch(
        "gaza",
        "2022-44-01",
        "2022-03-01");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  @Test
  void testHandleSearchWithStringAsDateParam() {
    RestApiResponse<Object> response = apiController.handleSearch(
        "gaza",
        "2022-11-02",
        "november 01");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  @Test
  void testHandleSearchWithServerFailure(){
    when(articleDbService.searchByInput(
        "gaza", "2023-11-01", "2023-11-20"
    )).thenThrow(MongoException.class);

    RestApiResponse<Object> response = apiController.handleSearch(
        "gaza",
        "2022-11-02",
        "2023-11-02");
    assertTrue(response instanceof RestApiFailureResponse);
  }


  @Test
  void testHandleSearchByLocationWithOnlyLocationSuccess() {
    List<ArticleEntity> mockArticles = Arrays.asList(
        new ArticleEntity(), new ArticleEntity()
    );

    when(articleDbService.searchByLocation("gaza")).thenReturn(mockArticles);

    RestApiResponse<Object> response = apiController.handleSearchByLocation(
        "gaza",
        null,
        null);

    assertTrue(response instanceof RestApiSuccessResponse);
    assertEquals(mockArticles, response.getData());
    verify(articleDbService).searchByLocation("gaza");
  }

  @Test
  void testHandleSearchByLocationWithLocationAndDateRangeSuccess() {
    List<ArticleEntity> mockArticles = Arrays.asList(
        new ArticleEntity(), new ArticleEntity(), new ArticleEntity()
    );

    when(articleDbService.searchByLocation(
        "gaza", "2023-11-01", "2023-11-20"
    )).thenReturn(mockArticles);

    RestApiResponse<Object> response = apiController.handleSearchByLocation(
        "gaza",
        "2023-11-01",
        "2023-11-20");

    assertTrue(response instanceof RestApiSuccessResponse);
    assertEquals(mockArticles, response.getData());
    verify(articleDbService).searchByLocation("gaza", "2023-11-01", "2023-11-20");
  }

  @Test
  void testHandleSearchByLocationWithNoRequestParamsFailure() {
    RestApiResponse<Object> response = apiController.handleSearchByLocation(null,null,null);
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  @Test
  void testHandleSearchByLocationWithEmptyInputFailure() {
    RestApiResponse<Object> response = apiController.handleSearchByLocation(" ",null,null);
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  @Test
  void testHandleSearchByLocationWithInvalidToDateParam() {
    RestApiResponse<Object> response = apiController.handleSearchByLocation(
        "gaza",
        "2022-01-01",
        "2022-03");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  @Test
  void testHandleSearchByLocationWithInvalidFromDateParam() {
    RestApiResponse<Object> response = apiController.handleSearchByLocation(
        "gaza",
        "2022-44-01",
        "2022-03-01");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  @Test
  void testHandleSearchByLocationWithStringAsDateParam() {
    RestApiResponse<Object> response = apiController.handleSearchByLocation(
        "gaza",
        "2022-11-02",
        "november 01");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  @Test
  void testHandleSearchByLocationWithServerFailure(){
    when(articleDbService.searchByLocation(
        "gaza", "2023-11-01", "2023-11-20"
    )).thenThrow(MongoException.class);

    RestApiResponse<Object> response = apiController.handleSearchByLocation(
        "gaza",
        "2022-11-02",
        "2023-11-02");
    assertTrue(response instanceof RestApiFailureResponse);
  }

  @Test
  void testHandleSearchByDateRangeSuccess(){
    List<ArticleEntity> mockArticles = Arrays.asList(
        new ArticleEntity(), new ArticleEntity(), new ArticleEntity()
    );
    when(articleDbService.searchByDateRange(
        "2022-06-20",
        "2022-11-13"
    )).thenReturn(mockArticles);

    RestApiResponse<Object> response = apiController.handleSearchByDateRange(
        "2022-06-20", "2022-11-13");

    assertTrue(response instanceof RestApiSuccessResponse);
    assertEquals(mockArticles, response.getData());
    verify(articleDbService).searchByDateRange("2022-06-20", "2022-11-13");
  }

  @Test
  void testHandleSearchByDateRangeWithEmptyParams() {
    RestApiResponse<Object> response = apiController.handleSearchByDateRange(null,null);
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  @Test
  void testHandleSearchByDateRangeWithInvalidToDateParam() {
    RestApiResponse<Object> response = apiController.handleSearchByDateRange(
        "2022-01-01",
        "2022-03");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  @Test
  void testHandleSearchByDateRangeWithInvalidFromDateParam() {
    RestApiResponse<Object> response = apiController.handleSearchByDateRange(
        "2022-44-01",
        "2022-03-01");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  @Test
  void testHandleSearchByDateRangeWithStringAsDateParam() {
    RestApiResponse<Object> response = apiController.handleSearchByDateRange(
        "2022-11-02",
        "november 01");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  @Test
  void testHandleSearchByDateRangeServerFailure() {
    when(articleDbService.searchByDateRange(
        "2022-11-01",
        "2022-11-03"
    )).thenThrow(MongoException.class);

    RestApiResponse<Object> response = apiController.handleSearchByDateRange(
        "2022-11-01",
        "2022-11-03");
    assertTrue(response instanceof RestApiFailureResponse);
  }

  // No need to test, only for development purposes
  @Test
  void handleProcess() {
  }
}