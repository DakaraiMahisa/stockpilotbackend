package com.stockpilot.backend.identity.audits.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class SessionRevokedEvent {

    private final UUID actorId;

    private final UUID tenantId;

    private final UUID targetUserId;

    private final String reason;

    private final String ipAddress;

    private final String userAgent;
}