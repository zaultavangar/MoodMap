package com.example.backend.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import com.example.backend.dbServices.ArticleDbService;
import com.example.backend.dbServices.FeatureDbUpdaterService;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.guardianService.GuardianService;
import com.example.backend.guardianService.responseRelated.AugmentedContentItem;
import com.example.backend.guardianService.responseRelated.AugmentedContentResponse;
import com.example.backend.nerService.ArticleNerProperties;
import com.example.backend.processors.Processor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class ProcessorTest {

    @Mock
    private GuardianService guardianService;

    @Mock
    private ArticleDbService articleDbService; // need this even though not used for tests to work

    @Mock
    private FeatureDbUpdaterService featureDbUpdaterService;

    @InjectMocks
    private Processor processor;

    @Test
    void testProcessArticles_ValidDateRangeWithArticles() {
        // Arrange
        try {
            String fromDate = "2023-01-01";
            String toDate = "2023-01-01";
            int totalArticles = 400;

            AugmentedContentResponse response = createMockResponse(totalArticles);
            when(guardianService.fetchArticlesByDateRange(fromDate, toDate, 1)).thenReturn(response);
            when(guardianService.fetchArticlesByDateRange(fromDate, toDate, 2)).thenReturn(response);
            when(guardianService.fetchArticlesByDateRange(fromDate, toDate, 3)).thenReturn(response);

            ArticleNerProperties articleNerProperties = ArticleNerProperties.builder()
                .numAssociatedFeatures(2)
                .sentimentScore(0.2)
                .locations(List.of("Israel", "Gaza"))
                .build();

            when(featureDbUpdaterService.updateFeaturesForArticle(any(ArticleEntity.class)))
                .thenReturn(articleNerProperties)
                .thenReturn(articleNerProperties)
                .thenReturn(articleNerProperties);

            processor.processArticles(fromDate, toDate, false);


            verify(guardianService, times(4)).fetchArticlesByDateRange(eq(fromDate), eq(toDate), anyInt());
        } catch (Exception e){
            System.out.println(e);
            fail();
        }

    }


    @Test
    void testProcessArticles_InvalidOrBlankDates() throws Exception {
        // Arrange
        String fromDate = "";
        String toDate = "2023-01-01";

        processor.processArticles(fromDate, toDate, false);

        verify(guardianService, never()).fetchArticlesByDateRange(anyString(), anyString(), anyInt());
    }

    @Test
    void testProcessArticles_ApiReturnsEmptyOptional() throws Exception {
        // Arrange
        String fromDate = "2023-01-01";
        String toDate = "2023-01-01";
        AugmentedContentResponse response = new AugmentedContentResponse();
        response.setStatus("fail");
        when(guardianService.fetchArticlesByDateRange(anyString(), anyString(), anyInt())).thenReturn(response);

        processor.processArticles(fromDate, toDate, false);

        verify(guardianService).fetchArticlesByDateRange(eq(fromDate), eq(toDate), eq(1));
    }

    @Test
    void testProcessArticle_WithAssociatedFeatures(){
        try {
            ArticleNerProperties articleNerProperties = ArticleNerProperties.builder()
                .numAssociatedFeatures(2)
                .sentimentScore(0.2)
                .locations(List.of("Israel", "Gaza"))
                .build();

            ArticleEntity expectedArticleEntity = ArticleEntity.builder()
                ._id(null)
                .webTitle("Israel says it will stop new aid crossing into Gaza Strip")
                .webPublicationDate(formatDate("2023-12-15T18:19:46Z"))
                .webUrl("https://www.theguardian.com/world/2023/dec/15/jake-sullivan-mahmoud-abbas-discuss-future-plans-gaza-strip")
                .thumbnail("www.thumb-nail.com")
                .bodyText("Article body text")
                .associatedLocations(List.of("Israel", "Gaza"))
                .sentimentScore(0.2)
                .build();

            Map<String, String> articleFields = Map.of("thumbnail", "www.thumb-nail.com", "bodyText", "Article Body Text");

            when(featureDbUpdaterService.updateFeaturesForArticle(any(ArticleEntity.class)))
                .thenReturn(articleNerProperties);

            AugmentedContentItem augmentedContentItem = new AugmentedContentItem(
                "world/2023/dec/15/jake-sullivan-mahmoud-abbas-discuss-future-plans-gaza-strip",
                "article",
                "world",
                "World news",
                "2023-12-15T18:19:46Z",
                "Israel says it will stop new aid crossing into Gaza Strip",
                "https://www.theguardian.com/world/2023/dec/15/jake-sullivan-mahmoud-abbas-discuss-future-plans-gaza-strip",
                "https://content.guardianapis.com/world/2023/dec/15/jake-sullivan-mahmoud-abbas-discuss-future-plans-gaza-strip",
                "pillar/news",
                "News",
                false,
                articleFields
            );

            ArticleEntity returnedArticle = processor.processArticle(augmentedContentItem);

            assertNull(returnedArticle.get_id());
            assertEquals(expectedArticleEntity.getWebTitle(), returnedArticle.getWebTitle());
            assertEquals(expectedArticleEntity.getWebPublicationDate(), returnedArticle.getWebPublicationDate());
            assertEquals(expectedArticleEntity.getWebUrl(), returnedArticle.getWebUrl());
            assertEquals(expectedArticleEntity.getThumbnail(), returnedArticle.getThumbnail());
            assertEquals("", returnedArticle.getBodyText());
            assertEquals(expectedArticleEntity.getAssociatedLocations(), returnedArticle.getAssociatedLocations());
            assertEquals(expectedArticleEntity.getSentimentScore(), returnedArticle.getSentimentScore());

        } catch (Exception e){
            fail();
        }
    }

    @Test
    void testProcessArticle_WithNoAssociatedLocations(){
        try {
            ArticleNerProperties articleNerProperties = ArticleNerProperties.builder()
                .numAssociatedFeatures(0)
                .sentimentScore(0.2)
                .locations(new ArrayList<>())
                .build();

            Map<String, String> articleFields = Map.of("thumbnail", "www.thumb-nail.com", "bodyText", "Article Body Text");

            when(featureDbUpdaterService.updateFeaturesForArticle(any(ArticleEntity.class)))
                .thenReturn(articleNerProperties);

            AugmentedContentItem augmentedContentItem = new AugmentedContentItem(
                "articleId",
                "article",
                "world",
                "World news",
                "2023-12-15T18:19:46Z",
                "No locations in title",
                "www.articleURL.com",
                "www.apiURL.com",
                "pillar/news",
                "News",
                false,
                articleFields
            );

            ArticleEntity returnedArticle = processor.processArticle(augmentedContentItem);

            assertNull(returnedArticle);
        } catch (Exception e){
            fail();
        }
    }

    private AugmentedContentResponse createMockResponse(int total) {
        Map<String, String> map1 = Map.of("thumbnail", "www.thumbnail1.com", "bodyText", "Article 1 body text");
        Map<String, String> map2 = Map.of("thumbnail", "www.thumbnail2.com", "bodyText", "Article 2 body text");
        AugmentedContentItem item1 = new AugmentedContentItem(
            "article1Id",
            "article",
            "world/news",
            "world",
            "2023-12-15T18:19:46Z",
            "article 1 title",
            "www.article1.com",
            "www.article1ApiUrl.com",
            "pillarId",
            "pillarName",
            false,
            map1
        );
        AugmentedContentItem item2 = new AugmentedContentItem(
            "article2Id",
            "article",
            "world/news",
            "world",
            "2023-11-15T18:19:46Z",
            "article 2 title",
            "www.article2.com",
            "www.article2ApiUrl.com",
            "pillarId",
            "pillarName",
            false,
            map2
        );

        List<AugmentedContentItem> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item2);

        AugmentedContentItem[] itemArray = new AugmentedContentItem[itemList.size()];

        return AugmentedContentResponse.builder()
            .status("ok")
            .userTier("user")
            .total(total)
            .startIndex(0)
            .pageSize(200)
            .pages(2)
            .orderBy("none")
            .results(itemList.toArray(itemArray))
            .build();
    }

    private LocalDateTime formatDate(String date) throws DateTimeParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(date, formatter);
    }

}
