package com.example.backend.nerService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.exceptions.HuggingFaceApiException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class NerServiceTest {

  private static final String API_URL = "https://api-inference.huggingface.co/models/nlptown/bert-base-multilingual-uncased-sentiment";

  @InjectMocks
  private NerService nerService;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private HuggingFaceConfig config;

  /**
   * Tests the successful execution of a sentiment analysis request using the Hugging Face API.
   * Validates the correct processing and mapping of sentiment scores from the API response.
   */
  @Test
  void testMakeHuggingFaceSentimentRequest() {

    String mockScores = "[[" +
        "{\"label\": \"1 star\", \"score\": 0.15}," +
        "{\"label\": \"2 stars\", \"score\": 0.1}," +
        "{\"label\": \"3 stars\", \"score\": 0.4}," +
        "{\"label\": \"4 stars\", \"score\": 0.3}," +
        "{\"label\": \"5 stars\", \"score\": 0.05}" +
        "]]";
    ResponseEntity<String> mockResponse = new ResponseEntity<>(mockScores, HttpStatus.OK);

    when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(String.class)))
        .thenReturn(mockResponse);

    assertDoesNotThrow(() -> {
      List<List<NerResponseScore>> result = nerService.makeHuggingFaceSentimentRequest("Text to analyze");

      assertFalse(result.isEmpty());
      assertFalse(result.get(0).isEmpty());
      assertEquals(5, result.get(0).size());
      assertEquals("1 star", result.get(0).get(0).getLabel());
      assertEquals(0.15, result.get(0).get(0).getScore());
      assertEquals("2 stars", result.get(0).get(1).getLabel());
      assertEquals(0.1, result.get(0).get(1).getScore());
      assertEquals("3 stars", result.get(0).get(2).getLabel());
      assertEquals(0.4, result.get(0).get(2).getScore());
      assertEquals("4 stars", result.get(0).get(3).getLabel());
      assertEquals(0.3, result.get(0).get(3).getScore());assertEquals("1 star", result.get(0).get(0).getLabel());
      assertEquals("5 stars", result.get(0).get(4).getLabel());
      assertEquals(0.05, result.get(0).get(4).getScore());
    });
    verify(restTemplate).postForEntity(eq(API_URL), any(HttpEntity.class), eq(String.class));
  }

  /**
   * Tests the behavior of the sentiment analysis request when receiving a non-2xx response from the Hugging Face API.
   * Expects an exception to be thrown, simulating API failure scenarios.
   */
  // HG = Hugging Face
  @Test
  void makeHuggingFaceSentimentRequestWhenNon2xxResponseFromHG() {
    ResponseEntity<String> mockErrorResponse = new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);

    when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(String.class)))
        .thenReturn(mockErrorResponse);

    assertThrows(HuggingFaceApiException.class, () -> {
      nerService.makeHuggingFaceSentimentRequest("Text to analyze");
    });
  }

  /**
   * Tests the getEntities method with empty input.
   * Ensures that no entities are extracted and an empty list is returned.
   */
  @Test
  void testGetEntitiesOnEmptyInput() {
    List<String> locations = nerService.getEntities(" ");
    assertEquals(new ArrayList<>(), locations);
  }

  /**
   * Tests the extraction of entities (locations) from an input without any identifiable locations.
   * Verifies the correctness of the empty result list.
   */
  @Test
  void testGetEntitiesOnInputWithNoLocations() {
    List<String> locations = nerService.getEntities("A stream of water");
    assertEquals(new ArrayList<>(), locations);
  }

  /**
   * Tests the entity extraction functionality for a given input text with identifiable locations.
   * Checks for accurate identification and extraction of location entities.
   */
  @Test
  void testGetEntitiesExtractsLocations() {
    List<String> locations = nerService.getEntities("Tension between Germany and Brazil escalates");
    assertEquals(List.of("Germany", "Brazil"), locations);
  }

  /**
   * Tests the entity extraction, including locations and nationalities, from a given input text.
   * Validates the extraction of multiple entity types from the input.
   */
  @Test
  void testGetEntitiesExtractsLocationsAndNationalities() {
    List<String> locations = nerService.getEntities(
        "Tension between Germany and Brazil escalates, deal with French looming");
    assertEquals(List.of("Germany", "Brazil", "France"), locations);
  }

  /**
   * Tests the successful calculation of an overall sentiment score from a given text.
   * Validates the accuracy and correctness of the sentiment score based on the API response.
   */
  @Test
  void getSentimentScoreSuccess() {
    String mockScores = "[[" +
        "{\"label\": \"1 star\", \"score\": 0.15}," +
        "{\"label\": \"2 stars\", \"score\": 0.1}," +
        "{\"label\": \"3 stars\", \"score\": 0.4}," +
        "{\"label\": \"4 stars\", \"score\": 0.3}," +
        "{\"label\": \"5 stars\", \"score\": 0.05}" +
        "]]";
    ResponseEntity<String> mockResponse = new ResponseEntity<>(mockScores, HttpStatus.OK);

    when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(String.class)))
        .thenReturn(mockResponse);

    assertDoesNotThrow(() -> {
      Double score = nerService.getSentimentScore("Article Title");
      assertEquals(0.5, score);
    });

    verify(restTemplate).postForEntity(eq(API_URL), any(HttpEntity.class), eq(String.class));
  }

  /**
   * Tests the calculation of sentiment score with an invalid format in the API response.
   * Ensures that the method handles format errors correctly, throwing an appropriate exception.
   */
  @Test
  void getSentimentScoreFailure() {
    String mockScores = "[[" +
        "{\"label\": \" star\", \"score\": 0.15}," + // bad format for first score
        "{\"label\": \"2 stars\", \"score\": 0.1}," +
        "{\"label\": \"3 stars\", \"score\": 0.4}," +
        "{\"label\": \"4 stars\", \"score\": 0.3}," +
        "{\"label\": \"5 stars\", \"score\": 0.05}" +
        "]]";
    ResponseEntity<String> mockResponse = new ResponseEntity<>(mockScores, HttpStatus.OK);

    when(restTemplate.postForEntity(eq(API_URL), any(HttpEntity.class), eq(String.class)))
        .thenReturn(mockResponse);

    assertThrows(NumberFormatException.class, () -> {
      nerService.getSentimentScore("Article Title");
    });

    verify(restTemplate).postForEntity(eq(API_URL), any(HttpEntity.class), eq(String.class));
  }
}