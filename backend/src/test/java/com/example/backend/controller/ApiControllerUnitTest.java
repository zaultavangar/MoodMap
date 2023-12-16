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

  /**
   * Tests if getFeatures method returns a successful response with the correct data.
   * Mocks featureDbService to return predefined data and checks the response.
   */
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

  /**
   * Tests if getFeatures method correctly handles server errors.
   * Mocks featureDbService to throw an exception and checks if a failure response is returned.
   */
  @Test
  void testGetFeaturesServerError() {
    when(featureDbService.getFeatures()).thenThrow(UncategorizedMongoDbException.class);

    RestApiResponse<Object> response = apiController.getFeatures();

    assertTrue(response instanceof RestApiFailureResponse);
    verify(featureDbService).getFeatures();
  }

  /**
   * Tests handleSearch method for success scenario with only input parameter.
   * Mocks articleDbService to return a predefined response and verifies the correctness of the returned data.
   */
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

  /**
   * Tests handleSearch method with both input and date range parameters for successful operation.
   * Mocks articleDbService to return specific data and verifies if the response is as expected.
   */
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

  /**
   * Tests handleSearch method for failure when no request parameters are provided.
   * Verifies that a failure response is returned and no interactions with articleDbService occur.
   */
  @Test
  void testHandleSearchWithNoRequestParamsFailure() {
    RestApiResponse<Object> response = apiController.handleSearch(null,null,null);
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  /**
   * Tests handleSearch method for failure when an empty input is provided.
   * Verifies that a failure response is returned and no interactions with articleDbService occur.
   */
  @Test
  void testHandleSearchWithEmptyInputFailure() {
    RestApiResponse<Object> response = apiController.handleSearch(" ",null,null);
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  /**
   * Tests handleSearch method for failure with an invalid 'toDate' parameter.
   * Verifies that a failure response is returned and no interactions with articleDbService occur.
   */
  @Test
  void testHandleSearchWithInvalidToDateParam() {
    RestApiResponse<Object> response = apiController.handleSearch(
        "gaza",
        "2022-01-01",
        "2022-03");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  /**
   * Tests handleSearch method for failure with an invalid 'fromDate' parameter.
   * Verifies that a failure response is returned and no interactions with articleDbService occur.
   */
  @Test
  void testHandleSearchWithInvalidFromDateParam() {
    RestApiResponse<Object> response = apiController.handleSearch(
        "gaza",
        "2022-44-01",
        "2022-03-01");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }


  /**
   * Tests handleSearch method for failure with a non-date string as a date parameter.
   * Verifies that a failure response is returned and no interactions with articleDbService occur.
   */
  @Test
  void testHandleSearchWithStringAsDateParam() {
    RestApiResponse<Object> response = apiController.handleSearch(
        "gaza",
        "2022-11-02",
        "november 01");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  /**
   * Tests handleSearch method for handling server failures during the search operation.
   * Mocks articleDbService to throw an exception and checks if a failure response is returned.
   */
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

  /**
   * Tests handleSearchByLocation method for success with only a location parameter.
   * Mocks articleDbService to return predefined data and checks the correctness of the response.
   */
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

  /**
   * Tests handleSearchByLocation method with both location and date range parameters for success.
   * Mocks articleDbService to return specific data and verifies the response.
   */
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


  /**
   * Tests handleSearchByLocation method for failure when no request parameters are provided.
   * Verifies that a failure response is returned and no interactions with articleDbService occur.
   */
  @Test
  void testHandleSearchByLocationWithNoRequestParamsFailure() {
    RestApiResponse<Object> response = apiController.handleSearchByLocation(null,null,null);
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  /**
   * Tests handleSearchByLocation method for failure with an empty location parameter.
   * Verifies that a failure response is returned and no interactions with articleDbService occur.
   */
  @Test
  void testHandleSearchByLocationWithEmptyInputFailure() {
    RestApiResponse<Object> response = apiController.handleSearchByLocation(" ",null,null);
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  /**
   * Tests handleSearchByLocation method for failure with invalid 'toDate' parameter.
   * Verifies that a failure response is returned and no interactions with articleDbService occur.
   */
  @Test
  void testHandleSearchByLocationWithInvalidToDateParam() {
    RestApiResponse<Object> response = apiController.handleSearchByLocation(
        "gaza",
        "2022-01-01",
        "2022-03");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  /**
   * Tests handleSearchByLocation method for failure with invalid 'fromDate' parameter.
   * Verifies that a failure response is returned and no interactions with articleDbService occur.
   */
  @Test
  void testHandleSearchByLocationWithInvalidFromDateParam() {
    RestApiResponse<Object> response = apiController.handleSearchByLocation(
        "gaza",
        "2022-44-01",
        "2022-03-01");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  /**
   * Tests handleSearchByLocation method for failure with non-date string as date parameter.
   * Verifies that a failure response is returned and no interactions with articleDbService occur.
   */
  @Test
  void testHandleSearchByLocationWithStringAsDateParam() {
    RestApiResponse<Object> response = apiController.handleSearchByLocation(
        "gaza",
        "2022-11-02",
        "november 01");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }


  /**
   * Tests handleSearchByLocation method for handling server failures during the location-based search.
   * Mocks articleDbService to throw an exception and checks if a failure response is returned.
   */
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


  /**
   * Tests handleSearchByDateRange method for success with valid date range parameters.
   * Mocks articleDbService to return specific data and verifies the correctness of the response.
   */
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

  /**
   * Tests handleSearchByDateRange method for failure with empty date range parameters.
   * Verifies that a failure response is returned and no interactions with articleDbService occur.
   */
  @Test
  void testHandleSearchByDateRangeWithEmptyParams() {
    RestApiResponse<Object> response = apiController.handleSearchByDateRange(null,null);
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  /**
   * Tests handleSearchByDateRange method for failure with invalid 'toDate' parameter.
   * Verifies that a failure response is returned and no interactions with articleDbService occur.
   */
  @Test
  void testHandleSearchByDateRangeWithInvalidToDateParam() {
    RestApiResponse<Object> response = apiController.handleSearchByDateRange(
        "2022-01-01",
        "2022-03");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  /**
   * Tests handleSearchByDateRange method for failure with invalid 'fromDate' parameter.
   * Verifies that a failure response is returned and no interactions with articleDbService occur.
   */
  @Test
  void testHandleSearchByDateRangeWithInvalidFromDateParam() {
    RestApiResponse<Object> response = apiController.handleSearchByDateRange(
        "2022-44-01",
        "2022-03-01");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  /**
   * Tests handleSearchByDateRange method for failure with non-date string as date parameter.
   * Verifies that a failure response is returned and no interactions with articleDbService occur.
   */
  @Test
  void testHandleSearchByDateRangeWithStringAsDateParam() {
    RestApiResponse<Object> response = apiController.handleSearchByDateRange(
        "2022-11-02",
        "november 01");
    assertTrue(response instanceof RestApiFailureResponse);
    verifyNoInteractions(articleDbService);
  }

  /**
   * Tests handleSearchByDateRange method for handling server failures during the date range search.
   * Mocks articleDbService to throw an exception and checks if a failure response is returned.
   */
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