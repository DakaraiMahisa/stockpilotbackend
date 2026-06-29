package com.stockpilot.backend.identity.application.service;

import com.stockpilot.backend.identity.audits.context.RequestAuditContext;
import com.stockpilot.backend.identity.audits.enums.SessionRevocationReason;
import com.stockpilot.backend.identity.audits.events.SessionRevokedEvent;
import com.stockpilot.backend.identity.domain.repository.RefreshTokenRepository;
import com.stockpilot.backend.identity.infrastructure.security.jwt.JwtService;
import com.stockpilot.backend.identity.usermanagement.entity.UserSession;
import com.stockpilot.backend.identity.usermanagement.repository.UserSessionRepository;
import com.stockpilot.backend.shared.exception.InvalidTokenException;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ApplicationEventPublisher eventPublisher;
    private final RequestAuditContext requestContext;
    private final UserSessionRepository userSessionRepository;

    @Transactional
    public void logoutAllDevices(String authHeader) {

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
        UUID tenantId = jwtService.extractTenantId(jwt);

        refreshTokenRepository.deleteAllByUserId(userId);

        userSessionRepository.revokeAllUserSessions(
                userId,
                tenantId,
                Instant.now()
        );

        eventPublisher.publishEvent(
                new SessionRevokedEvent(
                        userId,
                        tenantId,
                        userId,
                        SessionRevocationReason.USER_LOGOUT.name(),
                        requestContext.getClientIp(),
                        requestContext.getUserAgent()
                )
        );
    }

    @Transactional
    public void logout(String authHeader) {

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
        UUID tenantId = jwtService.extractTenantId(jwt);
        UUID sessionId = jwtService.extractSessionId(jwt);

        UserSession session = userSessionRepository
                .findByIdAndUserIdAndTenantId(
                        sessionId,
                        userId,
                        tenantId
                )
                .orElseThrow(() ->
                        new InvalidTokenException(
                                "Session not found"
                        ));

        if (session.isRevoked()) {
            return;
        }

        refreshTokenRepository.deleteBySessionId(sessionId);

        session.setRevoked(true);
        session.setRevokedAt(Instant.now());

        userSessionRepository.save(session);

        eventPublisher.publishEvent(
                new SessionRevokedEvent(
                        userId,
                        tenantId,
                        userId,
                        SessionRevocationReason.USER_LOGOUT.name(),
                        requestContext.getClientIp(),
                        requestContext.getUserAgent()
                )
        );
    }
}
