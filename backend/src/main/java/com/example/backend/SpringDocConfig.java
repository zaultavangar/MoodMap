package com.example.backend;

import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

  @Bean
  public GroupedOpenApi api() {
    return GroupedOpenApi.builder()
        .group("springdoc")
        .packagesToScan("com.example.backend.controller")
        .build();
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new io.swagger.v3.oas.models.info.Info()
            .title("Example API")
            .version("v1"));
  }
}
