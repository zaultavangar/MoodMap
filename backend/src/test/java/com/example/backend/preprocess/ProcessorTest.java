package com.example.backend.preprocess;

import static org.mockito.Mockito.*;

import com.example.backend.guardianService.GuardianService;
import com.example.backend.guardianService.responseRelated.AugmentedContentResponse;
import com.example.backend.processors.Processor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class ProcessorTest {

    @Mock
    private GuardianService guardianService;
    @InjectMocks
    private Processor processor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processArticles_ValidDateRangeWithArticles() throws Exception {
        // Arrange
        String fromDate = "2023-01-01";
        String toDate = "2023-01-01";
        int totalArticles = 400;

        AugmentedContentResponse response = createMockResponse(totalArticles);
        when(guardianService.fetchArticlesByDateRange(fromDate, toDate, 1)).thenReturn(response);
        when(guardianService.fetchArticlesByDateRange(fromDate, toDate, 2)).thenReturn(response);
        processor.processArticles(fromDate, toDate, false);
        verify(guardianService, times(2)).fetchArticlesByDateRange(eq(fromDate), eq(toDate), anyInt());
    }

    private AugmentedContentResponse createMockResponse(int total) {
        AugmentedContentResponse response = new AugmentedContentResponse();
        response.setStatus("ok");
        response.setTotal(total);
        return response;
    }

    @Test
    void processArticles_InvalidOrBlankDates() throws Exception {
        // Arrange
        String fromDate = "";
        String toDate = "2023-01-01";

        processor.processArticles(fromDate, toDate, false);
        verify(guardianService, never()).fetchArticlesByDateRange(anyString(), anyString(), anyInt());
    }

    @Test
    void processArticles_ApiReturnsEmptyOptional() throws Exception {
        // Arrange
        String fromDate = "2023-01-01";
        String toDate = "2023-01-01";
        AugmentedContentResponse response = new AugmentedContentResponse();
        response.setStatus("fail");
        when(guardianService.fetchArticlesByDateRange(anyString(), anyString(), anyInt())).thenReturn(response);

        processor.processArticles(fromDate, toDate, false);
        verify(guardianService).fetchArticlesByDateRange(eq(fromDate), eq(toDate), eq(1));
    }

}
