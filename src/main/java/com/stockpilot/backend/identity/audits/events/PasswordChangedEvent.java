package com.stockpilot.backend.identity.audits.events;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PasswordChangedEvent(

        UUID userId,

        UUID tenantId,

        String ipAddress,

        String userAgent

) {
}