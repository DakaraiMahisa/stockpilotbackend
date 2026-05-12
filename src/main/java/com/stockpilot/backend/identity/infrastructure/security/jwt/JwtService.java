package com.stockpilot.backend.identity.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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

    public String generateAccessToken(UserDetails user, UUID tenantId, List<String> permissions) {
        Instant now = Instant.now();
        Instant expiryTime = now.plus(TOKEN_EXPIRY_MINUTES, ChronoUnit.MINUTES);

        try {
            return Jwts.builder()
                    .subject(user.getUsername())
                    .claim(CLAIM_TENANT_ID, tenantId.toString())
                    .claim(CLAIM_PERMISSIONS, permissions)
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(expiryTime))
                    .signWith(getSigningKey())
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token for user: {}", user.getUsername(), e);
            throw new RuntimeException("Failed to generate JWT token", e);
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
        try {
            Claims claims = getClaimsFromToken(token);
            String tenantIdStr = claims.get(CLAIM_TENANT_ID, String.class);
            return tenantIdStr != null ? UUID.fromString(tenantIdStr) : null;
        } catch (Exception e) {
            log.error("Error extracting tenant ID from token", e);
            return null;
        }
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

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    private SecretKey getSigningKey() {
        byte[] decodedKey = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(decodedKey);
    }
}