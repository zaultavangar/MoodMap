package com.example.backend.guardianService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import the.guardian.api.client.GuardianApi;

// STATUS: Not tested
@Configuration
public class GuardianServiceConfig {

    @Value("${guardian.key}")
    private String guardianKey;

    @Bean
    public GuardianApi guardianApi() {
        return new GuardianApi(guardianKey);
    }
}
