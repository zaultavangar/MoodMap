package com.example.backend.scheduledTask;

import com.example.backend.processors.Processor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

// STATUS: Not officially tested
@Component
public class ScheduledArticleTask {

    @Resource
    private Processor processor;

    @Scheduled(cron = "0 0 5 * * *", zone = "America/New_York")
    public void scheduledTask() {
        System.out.println("Running scheduled task at 5 AM EST");
        // Your task logic here
//        dailyProcessor.processArticles("2023-11-20", "2023-11-21", false);
    }
}
