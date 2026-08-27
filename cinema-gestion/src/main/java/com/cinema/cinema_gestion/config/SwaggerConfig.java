package com.cinema.cinema_gestion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;

/**
 * Documentation OpenAPI / Swagger UI : schéma Bearer JWT.
 */
@Configuration
public class SwaggerConfig {
    /**
     * @return description OpenAPI de l'API Cinema Gestion
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Cinema Gestion API")
                        .description("API for the Cinema Gestion system")
                        .version("1.0"));
    }

    /**
     * @return schéma HTTP Bearer (JWT)
     */
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}
