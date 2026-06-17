package com.stockpilot.backend.identity.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PathsConfig {

    @Bean(name = "publicPaths")
    public List<String> publicPaths() {
        return List.of(
                "/api/v1/auth/login/public",
                "/api/v1/auth/register/public",
                "/api/v1/auth/verify-email/public",
                "/api/v1/auth/refresh/public",
                "/api/v1/auth/logout/public",
                "/api/v1/auth/forgot-password/public",
                "/api/v1/auth/reset-password/public",
                "/api/v1/auth/accept-invitation",
                "/api/csrf-token/public",
                "/actuator/health",
                "/error",
                "/api/swagger-ui.html",
                "/swagger-ui/**",
                "/api/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**"
        );
    }

    @Bean(name = "securedPaths")
    public List<String> securedPaths() {
        return List.of(
                "/api/v1/users/**"
        );
    }

}