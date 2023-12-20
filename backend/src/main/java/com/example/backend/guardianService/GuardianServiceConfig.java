package com.example.backend.guardianService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import the.guardian.api.client.GuardianApi;

/**
 * Configuration class for The Guardian service.
 * Configures and provides a GuardianApi bean.
 */
@Configuration
public class GuardianServiceConfig {

    @Value("${guardian.key}")
    private String guardianKey;

    /**
     * Provides a GuardianApi bean configured with the necessary API key.
     *
     * @return A GuardianApi instance.
     */
    @Bean
    public GuardianApi guardianApi() {
        return new GuardianApi(guardianKey);
    }
}
