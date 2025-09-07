package com.jwt.jwt.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI jwtAuthOpenAPI(){
        return new OpenAPI().info(
                new Info().title("JWT")
                        .description("Authentication endpoints for register, login and token refresh")
                        .version("1.1.0")
        );
    }
}
