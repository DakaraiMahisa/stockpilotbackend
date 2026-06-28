package com.stockpilot.backend.identity.auth.service;


import com.stockpilot.backend.common.fixtures.CurrentUserPrincipalFixture;
import com.stockpilot.backend.common.utils.JwtTestConstants;
import com.stockpilot.backend.identity.domain.model.CurrentUserPrincipal;
import com.stockpilot.backend.identity.infrastructure.security.jwt.JwtProperties;
import com.stockpilot.backend.identity.infrastructure.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;

    private CurrentUserPrincipal principal;

    private UUID sessionId;

    private String token;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService(
                new JwtProperties(
                        JwtTestConstants.TEST_SECRET,
                        JwtTestConstants.TOKEN_EXPIRATION
                )
        );

        principal = CurrentUserPrincipalFixture.owner();

        sessionId = UUID.randomUUID();

        token = jwtService.generateAccessToken(
                principal,
                sessionId
        );
    }

    @Nested
    @DisplayName("Access Token Generation")
    class GenerateTokenTests {

        @Test
        @DisplayName("Should generate a valid JWT access token")
        void shouldGenerateAccessToken() {

            assertThat(token)
                    .isNotBlank();
        }
    }

    @Nested
    @DisplayName("Token Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should validate a generated token")
        void shouldValidateGeneratedToken() {

            assertThat(jwtService.validateToken(token))
                    .isTrue();
        }

        @Test
        @DisplayName("Should reject malformed token")
        void shouldRejectMalformedToken() {

            assertThat(jwtService.validateToken("invalid.jwt.token"))
                    .isFalse();
        }

        @Test
        @DisplayName("Should reject empty token")
        void shouldRejectEmptyToken() {

            assertThat(jwtService.validateToken(""))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("Claim Extraction")
    class ClaimExtractionTests {

        @Test
        @DisplayName("Should extract email")
        void shouldExtractEmail() {

            assertThat(jwtService.extractEmail(token))
                    .isEqualTo(principal.getEmail());
        }

        @Test
        @DisplayName("Should extract tenant id")
        void shouldExtractTenantId() {

            assertThat(jwtService.extractTenantId(token))
                    .isEqualTo(principal.getTenantId());
        }

        @Test
        @DisplayName("Should extract user id")
        void shouldExtractUserId() {

            assertThat(jwtService.extractUserId(token))
                    .isEqualTo(principal.getId());
        }

        @Test
        @DisplayName("Should extract session id")
        void shouldExtractSessionId() {

            assertThat(jwtService.extractSessionId(token))
                    .isEqualTo(sessionId);
        }

        @Test
        @DisplayName("Should extract permissions")
        void shouldExtractPermissions() {

            assertThat(jwtService.extractPermissions(token))
                    .containsExactlyInAnyOrderElementsOf(
                            principal.getPermissions()
                    );
        }

        @Test
        @DisplayName("Should reconstruct authenticated principal from token")
        void shouldExtractCurrentUserPrincipal() {

            CurrentUserPrincipal extracted =
                    jwtService.extractUserSession(token);

            assertThat(extracted.getId())
                    .isEqualTo(principal.getId());

            assertThat(extracted.getTenantId())
                    .isEqualTo(principal.getTenantId());

            assertThat(extracted.getSessionId())
                    .isEqualTo(sessionId);

            assertThat(extracted.getEmail())
                    .isEqualTo(principal.getEmail());

            assertThat(extracted.getPermissions())
                    .containsExactlyInAnyOrderElementsOf(
                            principal.getPermissions()
                    );

            assertThat(extracted.isEnabled())
                    .isTrue();
        }
    }
}