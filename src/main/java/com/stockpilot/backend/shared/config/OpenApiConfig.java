package com.stockpilot.backend.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(apiInfo())
                .components(securityComponents())
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                );
    }

    private Info apiInfo() {

        return new Info()
                .title("StockPilot API")
                .version("1.0.0")
                .description(
                        "REST API for the StockPilot SME Inventory " +
                                "and Sales Management Platform."
                )
                .contact(
                        new Contact()
                                .name("StockPilot")
                                .email("dakaraimahisa02@gmail.com")
                );
    }

    private Components securityComponents() {

        return new Components()
                .addSecuritySchemes(
                        "bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description(
                                        "JWT Bearer token used to authenticate API requests."
                                )
                );
    }
}