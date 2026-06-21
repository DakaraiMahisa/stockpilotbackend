package com.stockpilot.backend.identity.audits.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class TokenRotatedEvent {

    private final UUID userId;

    private final UUID tenantId;

    private final String userAgent;

    private final String ipAddress;
}