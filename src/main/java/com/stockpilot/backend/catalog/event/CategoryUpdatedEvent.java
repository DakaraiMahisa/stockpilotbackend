package com.stockpilot.backend.catalog.event;

import java.time.Instant;
import java.util.UUID;

public record CategoryUpdatedEvent(

        UUID categoryId,

        UUID tenantId,

        UUID updatedBy,

        Instant occurredAt

) {

    public static CategoryUpdatedEvent of(
            UUID categoryId,
            UUID tenantId,
            UUID updatedBy
    ) {
        return new CategoryUpdatedEvent(
                categoryId,
                tenantId,
                updatedBy,
                Instant.now()
        );
    }
}