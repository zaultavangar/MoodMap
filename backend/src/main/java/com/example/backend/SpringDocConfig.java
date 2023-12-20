package com.example.backend;

import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration class for SpringDoc.
 * Sets up OpenAPI for API documentation.
 */
@Configuration
public class SpringDocConfig {

  /**
   * Configures GroupedOpenApi for API documentation.
   *
   * @return A GroupedOpenApi instance.
   */
  @Bean
  public GroupedOpenApi api() {
    return GroupedOpenApi.builder()
        .group("springdoc")
        .packagesToScan("com.example.backend.controller")
        .build();
  }

  /**
   * Creates a custom OpenAPI configuration.
   *
   * @return An OpenAPI instance with custom settings.
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new io.swagger.v3.oas.models.info.Info()
            .title("Example API")
            .version("v1"));
  }
}
