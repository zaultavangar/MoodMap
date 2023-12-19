package com.example.backend.guardianService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.exceptions.GuardianApiException;
import com.example.backend.guardianService.responseRelated.AugmentedContentResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import the.guardian.api.client.GuardianApi;
import the.guardian.api.entity.Content;
import the.guardian.api.http.content.ContentResponse;

@ExtendWith(MockitoExtension.class)
public class GuardianServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private GuardianService guardianService;

  @Test
  void testFetchArticlesByDateRangeSuccess() throws Exception {
    String fromDate = "2023-01-01";
    String toDate = "2023-01-10";
    int pageNum = 1;

    String jsonResponse = "{\"response\": {\"results\": [], \"total\": 0}}";
    ResponseEntity<String> mockResponse = ResponseEntity.ok(jsonResponse);

    when(restTemplate.getForEntity(any(String.class), eq(String.class)))
        .thenReturn(mockResponse);

    // Act
    AugmentedContentResponse result = guardianService.fetchArticlesByDateRange(fromDate, toDate, pageNum);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.getTotal());
    assertEquals(0, result.getResults().length);

  }

  @Test
  void testFetchArticlesByDateRangeFailure() {
    String fromDate = "2023-01-01";
    String toDate = "2023-01-10";
    int pageNum = 1;

    ResponseEntity<String> mockErrorResponse = ResponseEntity.status(500).body("Internal Server Error");

    when(restTemplate.getForEntity(any(String.class), eq(String.class)))
        .thenReturn(mockErrorResponse);

    assertThrows(GuardianApiException.class, () -> guardianService.fetchArticlesByDateRange(fromDate, toDate, pageNum));

  }
}