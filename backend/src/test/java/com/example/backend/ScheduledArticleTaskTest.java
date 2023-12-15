package com.example.backend;

import com.example.backend.processors.Processor;
import com.example.backend.scheduledTask.ScheduledArticleTask;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.*;

class ScheduledArticleTaskTest {

    @Mock
    private Processor processor;

    @InjectMocks
    private ScheduledArticleTask scheduledArticleTask;

    public ScheduledArticleTaskTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testScheduledTask() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate nextDay = today.plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String expectedFromDate = today.format(formatter);
        String expectedToDate = nextDay.format(formatter);

        scheduledArticleTask.scheduledTask();
        verify(processor, times(1)).processArticles(eq(expectedFromDate), eq(expectedToDate), eq(false));
    }
}
