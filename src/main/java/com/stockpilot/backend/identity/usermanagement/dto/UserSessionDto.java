package com.stockpilot.backend.identity.usermanagement.dto;

import java.time.Instant;
import java.util.UUID;

public record UserSessionDto(
        UUID id,
        String ipAddress,
        String userAgent,
        Instant lastUsedAt,
        Instant expiresAt
) {
}
