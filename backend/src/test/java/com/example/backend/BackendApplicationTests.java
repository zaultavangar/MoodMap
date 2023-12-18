package com.example.backend;

import com.example.backend.controller.ApiControllerIntegrationTest;
import com.example.backend.controller.ApiControllerRandomTest;
import com.example.backend.controller.ApiControllerUnitTest;
import com.example.backend.dbServices.ArticleDbServiceTest;
import com.example.backend.dbServices.FeatureDbServiceTest;
import com.example.backend.dbServices.FeatureDbUpdaterServiceTest;
import com.example.backend.entity.ArticleEntityTest;
import com.example.backend.entity.FeatureEntityTest;
import com.example.backend.geocodingService.GeocodingServiceTest;
import com.example.backend.guardianService.GuardianServiceTest;
import com.example.backend.jsonUtility.JsonUtilityTest;
import com.example.backend.nerService.NerServiceTest;
import com.example.backend.processor.ProcessorTest;
import com.example.backend.scheduledTask.ScheduledArticleTaskTest;
import com.example.backend.validator.RequestValidatorTest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    ApiControllerIntegrationTest.class,
    ApiControllerRandomTest.class,
    ApiControllerUnitTest.class,
    ArticleDbServiceTest.class,
    FeatureDbServiceTest.class,
    FeatureDbUpdaterServiceTest.class,
    ArticleEntityTest.class,
    FeatureEntityTest.class,
    GeocodingServiceTest.class,
    GuardianServiceTest.class,
    JsonUtilityTest.class,
    NerServiceTest.class,
    ProcessorTest.class,
    ScheduledArticleTaskTest.class,
    RequestValidatorTest.class

})
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
