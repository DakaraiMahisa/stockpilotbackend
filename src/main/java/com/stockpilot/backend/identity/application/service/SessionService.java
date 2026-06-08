package com.stockpilot.backend.identity.application.service;

import com.stockpilot.backend.identity.domain.repository.RefreshTokenRepository;
import com.stockpilot.backend.identity.infrastructure.security.jwt.JwtService;
import com.stockpilot.backend.shared.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public void revoke(String authHeader) {

        if (!StringUtils.hasText(authHeader)
                || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException(
                    "Missing or invalid Authorization header"
            );
        }

        String jwt = authHeader.substring(7);

        if (!jwtService.validateToken(jwt)) {
            throw new InvalidTokenException(
                    "Invalid or expired access token"
            );
        }

        UUID userId = jwtService.extractUserId(jwt);

        refreshTokenRepository.deleteAllByUserId(userId);
    }
}
