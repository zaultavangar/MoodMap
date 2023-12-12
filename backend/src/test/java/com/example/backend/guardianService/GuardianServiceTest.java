package com.example.backend.guardianService;

import static org.junit.jupiter.api.Assertions.*;
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
import the.guardian.api.client.GuardianApi;
import the.guardian.api.entity.Content;
import the.guardian.api.http.content.ContentResponse;

@ExtendWith(MockitoExtension.class)
class GuardianServiceTest {

  @Mock
  private GuardianApi guardianClient;

  @Mock
  private Content content;

  @InjectMocks
  private GuardianService guardianService;

  @Test
  void testFetchArticlesByDateRangeSuccess() throws Exception {
//    AugmentedContentResponse mockResponse = new AugmentedContentResponse();
//    when(guardianClient.content()).thenReturn(content);
//    when(content.fetch()).thenReturn(mockResponse);
//
//    AugmentedContentResponse result = guardianService.fetchArticlesByDateRange("2023-01-01", "2023-03-31", 5);
//
//    assertNotNull(result);
//    assertEquals(mockResponse, result);
//    verify(guardianClient).content();

  }

  @Test
  void testFetchArticlesByDateRangeFailure() throws Exception {
//    when(guardianClient.content()).thenReturn(content);
//    when(content.fetch()).thenThrow(new UnirestException("Guardian connection error"));
//
//    assertThrows(GuardianApiException.class, () -> {
//      guardianService.fetchArticlesByDateRange("2023-01-01", "2023-03-31", 1);
//    });
//
//    verify(guardianClient).content();

  }
}