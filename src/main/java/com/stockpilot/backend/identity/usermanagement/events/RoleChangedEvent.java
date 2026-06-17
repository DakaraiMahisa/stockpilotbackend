package com.stockpilot.backend.identity.usermanagement.events;

import java.time.Instant;
import java.util.UUID;

public record RoleChangedEvent(

        UUID userId,

        UUID tenantId,

        String previousRole,

        String newRole,

        Instant occurredAt

) {

    public RoleChangedEvent(
            UUID userId,
            UUID tenantId,
            String previousRole,
            String newRole
    ) {
        this(
                userId,
                tenantId,
                previousRole,
                newRole,
                Instant.now()
        );
    }
}