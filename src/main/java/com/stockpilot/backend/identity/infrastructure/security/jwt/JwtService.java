package com.stockpilot.backend.identity.infrastructure.security.jwt;

import com.stockpilot.backend.identity.domain.model.CurrentUserPrincipal;
import com.stockpilot.backend.shared.exception.InvalidTokenException;
import com.stockpilot.backend.shared.exception.TokenGenerationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class JwtService {

    private static final String CLAIM_TENANT_ID = "tenantId";
    private static final String CLAIM_PERMISSIONS = "permissions";
    private static final int TOKEN_EXPIRY_MINUTES = 15;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateAccessToken(CurrentUserPrincipal currentUserPrincipal) {
        Instant now = Instant.now();
        Instant expiryTime = now.plus(TOKEN_EXPIRY_MINUTES, ChronoUnit.MINUTES);

        try {
            return Jwts.builder()
                    .header().add("typ", "JWT").and()
                    .subject(currentUserPrincipal.getUsername())
                    .claim("user_id", currentUserPrincipal.getId().toString())
                    .claim(CLAIM_TENANT_ID, currentUserPrincipal.getTenantId().toString())
                    .claim(CLAIM_PERMISSIONS, currentUserPrincipal.getPermissions()) // List of Strings
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(expiryTime))
                    .signWith(getSigningKey(), Jwts.SIG.HS256) // Modern JJWT syntax
                    .compact();
        } catch (Exception e) {
            log.error("JWT Generation failed for: {}", currentUserPrincipal.getUsername(), e);
            throw new TokenGenerationException("Could not create secure session", e);
        }
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("JWT token is empty or null");
            return false;
        }
    }

    public String extractEmail(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error extracting email from token", e);
            return null;
        }
    }

    public UUID extractTenantId(String token) {
        Claims claims = getClaimsFromToken(token);

        String tenantId = claims.get(
                CLAIM_TENANT_ID,
                String.class
        );

        if (tenantId == null) {
            throw new InvalidTokenException(
                    "Tenant ID claim not found"
            );
        }

        return UUID.fromString(tenantId);
    }

    public UUID extractUserId(String token) {
        Claims claims = getClaimsFromToken(token);

        String userId = claims.get("user_id", String.class);

        if (userId == null) {
            throw new InvalidTokenException(
                    "User ID claim not found"
            );
        }

        return UUID.fromString(userId);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get(CLAIM_PERMISSIONS, List.class);
        } catch (Exception e) {
            log.error("Error extracting permissions from token", e);
            return List.of();
        }
    }

    public CurrentUserPrincipal extractUserSession(String token) {
        Claims claims = getClaimsFromToken(token);
        return CurrentUserPrincipal.builder()
                .id(UUID.fromString(claims.get("user_id", String.class)))
                .tenantId(UUID.fromString(claims.get(CLAIM_TENANT_ID, String.class)))
                .email(claims.getSubject())
                .permissions(new HashSet<>(claims.get(CLAIM_PERMISSIONS, List.class)))
                .enabled(true)
                .build();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}