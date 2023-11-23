package com.example.backend.guardianService;

import com.example.backend.entity.ArticleEntity;
import com.example.backend.exceptions.GuardianApiException;
import com.example.backend.guardianService.responseRelated.AugmentedContentItem;
import com.example.backend.guardianService.responseRelated.AugmentedContentResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import the.guardian.api.client.GuardianApi;
import the.guardian.api.entity.Content;
import the.guardian.api.http.AbstractResponse;
import the.guardian.api.http.content.ContentResponse;
import the.guardian.api.http.editions.EditionsResponse;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class GuardianService {

    @Resource
    private GuardianApi guardianClient;

    public ContentResponse fetchArticlesByDateRange(String fromDate, String toDate) throws Exception{
      // Error handling for dates?
      Content content = guardianClient.content();
      content.setFromDate(fromDate);
      content.setToDate(toDate);
      content.setSection("world");
      content.setPage(1);
      content.setPageSize(5); // TODO: FIX
      content.setShowFields("trailText,bodyText,thumbnail");

      AbstractResponse response = content.fetch();
      if (response instanceof ContentResponse){
        ContentResponse contentRes = (ContentResponse) response;
        return contentRes;
      } 
      throw new GuardianApiException("Unable to retrieve articles from the Guardian API.");
        
    }

}
