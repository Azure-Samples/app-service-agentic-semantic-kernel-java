package com.example.crudtaskswithagent.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for API documentation.
 * Configures the OpenAPI schema that can be consumed by Foundry agents.
 * The server URL is dynamically determined by default.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .version("1.0.0")
                        .description("A simple task management API for Foundry agents"));
    }
}
