package com.stockpilot.backend.identity.infrastructure.security.csrf;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler
        implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex)
            throws IOException {

        response.setContentType("application/json");

        if (ex instanceof MissingCsrfTokenException) {

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

            response.getWriter().write("""
                    {
                        "code":"CSRF_TOKEN_MISSING",
                        "message":"CSRF token is missing"
                    }
                    """);

            return;
        }

        if (ex instanceof InvalidCsrfTokenException) {

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

            response.getWriter().write("""
                    {
                        "code":"CSRF_TOKEN_INVALID",
                        "message":"CSRF token is invalid"
                    }
                    """);

            return;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        response.getWriter().write("""
                {
                    "code":"ACCESS_DENIED",
                    "message":"Access denied"
                }
                """);
    }
}
