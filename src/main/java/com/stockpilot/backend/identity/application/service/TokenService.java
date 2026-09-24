package com.stockpilot.backend.identity.application.service;

import com.stockpilot.backend.identity.application.dto.RefreshTokenRequest;
import com.stockpilot.backend.identity.application.dto.TokenResponse;
import com.stockpilot.backend.identity.audits.context.RequestAuditContext;
import com.stockpilot.backend.identity.audits.events.TokenRotatedEvent;
import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.model.CurrentUserPrincipal;
import com.stockpilot.backend.identity.domain.repository.RoleRepository;
import com.stockpilot.backend.identity.domain.repository.UserRepository;
import com.stockpilot.backend.identity.infrastructure.security.RefreshTokenHasher;
import com.stockpilot.backend.identity.infrastructure.security.jwt.JwtService;
import com.stockpilot.backend.identity.exception.InvalidCredentialsException;
import com.stockpilot.backend.identity.usermanagement.entity.UserSession;
import com.stockpilot.backend.identity.usermanagement.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final UserSessionRepository userSessionRepository;
    private final RefreshTokenHasher refreshTokenHasher;
    private final UserRepository userRepository;
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

        String refreshTokenHash =
                refreshTokenHasher.hash(requestRefreshToken);

        UserSession session =
                userSessionRepository
                        .findByRefreshTokenHashAndRevokedFalse(
                                refreshTokenHash
                        )
                        .orElseThrow(() ->
                                new InvalidCredentialsException(
                                        "Invalid refresh token"
                                ));

        Instant now = Instant.now();

        if (session.getExpiresAt().isBefore(now)) {

            session.setRevoked(true);
            session.setRevokedAt(now);

            userSessionRepository.save(session);

            throw new InvalidCredentialsException(
                    "Refresh token has expired"
            );
        }

        User user =
                userRepository.findById(session.getUserId())
                        .orElseThrow(() ->
                                new InvalidCredentialsException(
                                        "User not found"
                                ));

        if (!user.getActive()) {
            throw new InvalidCredentialsException(
                    "User is not active"
            );
        }

        Set<String> permissions =
                roleRepository.findPermissionsByRoleId(
                        user.getRole().getId()
                );

        CurrentUserPrincipal currentUserPrincipal =
                CurrentUserPrincipal.fromUser(
                        user,
                        permissions
                );

        /*
         * Rotate refresh token.
         */
        String newRefreshToken =
                generateRefreshToken();

        session.setRefreshTokenHash(
                refreshTokenHasher.hash(newRefreshToken)
        );

        session.setLastUsedAt(now);

        session.setExpiresAt(
                now.plus(7, ChronoUnit.DAYS)
        );

        userSessionRepository.save(session);

        /*
         * Generate new access token for the
         * same authenticated session.
         */
        String newAccessToken =
                jwtService.generateAccessToken(
                        currentUserPrincipal,
                        session.getId()
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
                .refreshToken(newRefreshToken)
                .build();
    }

    private String generateRefreshToken() {

        byte[] bytes = new byte[32];

        new SecureRandom().nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
