package com.stockpilot.backend.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 configuration for Swagger documentation.
 * Configures API info, security schemes (JWT), and documentation metadata.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures OpenAPI documentation with JWT security scheme.
     *
     * @return customized OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .components(securityComponents())
                .addSecurityItem(new SecurityRequirement().addList("Bearer JWT"));
    }

    /**
     * Creates API information metadata.
     *
     * @return Info object with API details
     */
    private Info apiInfo() {
        return new Info()
                .title("StockPilot API")
                .version("1.0.0")
                .description("SME Inventory and Sales Management System REST API")
                .contact(new Contact()
                        .name("StockPilot Support")
                        .email("support@stockpilot.com")
                        .url("https://stockpilot.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }

    /**
     * Configures security components for JWT authentication.
     *
     * @return Components with JWT security scheme
     */
    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("Bearer JWT", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token for API authentication"));
    }
}

