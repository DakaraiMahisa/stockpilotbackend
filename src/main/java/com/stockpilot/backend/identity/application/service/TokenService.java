package com.stockpilot.backend.identity.application.service;

import com.stockpilot.backend.identity.application.dto.RefreshTokenRequest;
import com.stockpilot.backend.identity.application.dto.TokenResponse;
import com.stockpilot.backend.identity.audits.context.RequestAuditContext;
import com.stockpilot.backend.identity.audits.events.TokenRotatedEvent;
import com.stockpilot.backend.identity.domain.entity.RefreshToken;
import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.model.CurrentUserPrincipal;
import com.stockpilot.backend.identity.domain.repository.RefreshTokenRepository;
import com.stockpilot.backend.identity.domain.repository.RoleRepository;
import com.stockpilot.backend.identity.infrastructure.security.jwt.JwtService;
import com.stockpilot.backend.shared.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RequestAuditContext requestContext;

    @Transactional
    public TokenResponse refreshToken(
            RefreshTokenRequest refreshTokenRequest
    ) {

        String requestRefreshToken =
                refreshTokenRequest.getRefreshToken();

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(requestRefreshToken)
                        .orElseThrow(() ->
                                new InvalidCredentialsException(
                                        "Invalid refresh token"
                                ));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);

            throw new InvalidCredentialsException(
                    "Refresh token has expired"
            );
        }

        User user = refreshToken.getUser();

        if (!user.getActive()) {
            throw new InvalidCredentialsException(
                    "User is not active"
            );
        }

        UUID sessionId = refreshToken.getSessionId();

        Set<String> permissions =
                roleRepository.findPermissionsByRoleId(
                        user.getRole().getId()
                );

        CurrentUserPrincipal currentUserPrincipal =
                CurrentUserPrincipal.fromUser(
                        user,
                        permissions
                );

        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken.setExpiryDate(
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        RefreshToken updatedRefreshToken =
                refreshTokenRepository.save(refreshToken);

        String newAccessToken =
                jwtService.generateAccessToken(
                        currentUserPrincipal,
                        sessionId
                );

        eventPublisher.publishEvent(
                new TokenRotatedEvent(
                        user.getId(),
                        user.getTenantId(),
                        requestContext.getUserAgent(),
                        requestContext.getClientIp()
                )
        );

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(updatedRefreshToken.getToken())
                .build();
    }

    private RefreshToken createAndPersistRefreshToken(
            User user,
            String deviceInfo
    ) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByUser(user)
                        .orElse(
                                RefreshToken.builder()
                                        .user(user)
                                        .build()
                        );

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(
                Instant.now().plus(7, ChronoUnit.DAYS)
        );
        refreshToken.setDeviceInfo(deviceInfo);

        return refreshTokenRepository.save(refreshToken);
    }
}
