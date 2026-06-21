package com.stockpilot.backend.identity.audits.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class InvitationAcceptedEvent {

    private final UUID userId;

    private final UUID tenantId;

    private final String email;

    private final String ipAddress;

    private final String userAgent;
}