package com.nocountry.equitrust.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        OpenAPI openAPI = new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );

        // Define tags in the desired order so Swagger UI shows them that way
        openAPI.setTags(List.of(
                new Tag().name("Authentication").description("Authentication and authorization endpoints"),
                new Tag().name("Horses Post").description("API for horse post management"),
                new Tag().name("Veterinary Records").description("APIs for veterinary records management"),
                new Tag().name("Admin - Horse Verification").description("Admin endpoints for horse verification management"),
                new Tag().name("Users").description("User management endpoints"),
                new Tag().name("Chat History").description("Chat history and messaging endpoints")
        ));

        return openAPI;
    }
}
