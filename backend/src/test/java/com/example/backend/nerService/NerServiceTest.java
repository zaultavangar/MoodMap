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
  @Test
  void testGetEntitiesOnEmptyInput() {
    List<String> locations = nerService.getEntities(" ");
    assertEquals(new ArrayList<>(), locations);
  }

  @Test
  void testGetEntitiesOnInputWithNoLocations() {
    List<String> locations = nerService.getEntities("A stream of water");
    assertEquals(new ArrayList<>(), locations);
  }

  @Test
  void testGetEntitiesExtractsLocations() {
    List<String> locations = nerService.getEntities("Tension between Germany and Brazil escalates");
    assertEquals(List.of("Germany", "Brazil"), locations);
  }

  @Test
  void testGetEntitiesExtractsLocationsAndNationalities() {
    List<String> locations = nerService.getEntities(
        "Tension between Germany and Brazil escalates, deal with French looming");
    assertEquals(List.of("Germany", "Brazil", "France"), locations);
  }
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