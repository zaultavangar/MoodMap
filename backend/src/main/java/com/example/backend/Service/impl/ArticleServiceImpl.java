package com.example.backend.Service.impl;

import com.example.backend.Repository.ArticleRepo;
import com.example.backend.Service.ArticleService;
import com.example.backend.entity.ArticleInfor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleRepo articleRepo;


    @Override
    public void insert(List<ArticleInfor> articleInforList) {
        if (articleInforList != null && !CollectionUtils.isEmpty(articleInforList)) {
            List<ArticleInfor> result = articleRepo.saveAll(articleInforList);
            System.out.println(articleInforList);
            return;
        }
    }
}
