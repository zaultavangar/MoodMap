package com.example.backend.Service.impl;

import com.example.backend.Repository.ArticleRepo;
import com.example.backend.Service.ArticleService;
import com.example.backend.entity.ArticleInfor;
import com.example.backend.guardianClient.GuardianService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import the.guardian.api.http.content.ContentResponse;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private ArticleRepo articleRepo;

    @Resource
    private GuardianService guardianService;

    private int INSERT_PAGE_SIZE = 200;


    public void saveArticle(String fromDate, String toDate, Integer page, Integer pageSize) {
        ContentResponse response = guardianService.fetchByContent(fromDate, toDate, 0, 0);
        if (response == null || !response.getStatus().equals("ok")) {
            System.out.println("Error of getting total number from guardian api");
            return;
        }
        int total = response.getTotal();
        System.out.println("Current total size is: \n" + total + "\n");
        for (int i = 1; i < (total / INSERT_PAGE_SIZE) + 2; i++) {
            String log1 = String.format("Start insert page:%d with pagesize:%d \n", i, INSERT_PAGE_SIZE);
            System.out.println(log1);
            ContentResponse currentResponse = guardianService.fetchByContent(fromDate, toDate, i, INSERT_PAGE_SIZE);
            if (currentResponse == null || !response.getStatus().equals("ok")) {
                System.out.println(currentResponse);
                break;
            }
            String log2 = String.format("Finish insert page:%d with pagesize:%d \n", i, INSERT_PAGE_SIZE);
            System.out.println(log2);
            insertArticle(currentResponse);
        }
    }

    public void insertArticle(ContentResponse response) {
        try {
            if (response == null || response.getResults() == null) {
                return;
            }
            List<ArticleInfor> articleInforList = List.of(response.getResults()).stream().map(contentItem -> {
                ArticleInfor current = new ArticleInfor();
                BeanUtils.copyProperties(contentItem,current);
                current.setWebPublicationDate(convertDate(contentItem.getWebPublicationDate()));
                return current;
            }).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(articleInforList)) {
                System.out.println("Filter out all articles");
                return;
            }
            articleRepo.saveAll(articleInforList);
        }catch (Exception e) {
            System.out.println("Error of inserting Articles into mongo db");
        }
    }

    private Date convertDate(String publishedDate) {
        Instant instant = Instant.parse(publishedDate);
        Date date = Date.from(instant);
        return date;
    }
}
