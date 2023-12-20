package com.example.backend.scheduledTask;

import com.example.backend.processors.Processor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Component for scheduling article processing tasks.
 */
@Component
public class ScheduledArticleTask {

    @Resource
    private Processor processor;

    /**
     * Executes a scheduled task every day at 5 AM EST to process articles.
     * Fetches and processes articles for the current day.
     */
    @Scheduled(cron = "0 0 5 * * *", zone = "America/New_York")
    public void scheduledTask() {
        System.out.println("Running scheduled task at 5 AM EST");
        // Get the current date
        LocalDate today = LocalDate.now();
        LocalDate nextDay = today.plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String currentDayString = today.format(formatter);
        String nextDayString = nextDay.format(formatter);
        processor.processArticles(currentDayString, nextDayString, false);
    }
}
