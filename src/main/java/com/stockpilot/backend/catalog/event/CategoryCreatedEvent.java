package com.stockpilot.backend.catalog.event;

import java.time.Instant;
import java.util.UUID;

public record CategoryCreatedEvent(

        UUID categoryId,

        UUID tenantId,

        UUID createdBy,

        Instant occurredAt

) {

    public static CategoryCreatedEvent of(
            UUID categoryId,
            UUID tenantId,
            UUID createdBy
    ) {
        return new CategoryCreatedEvent(
                categoryId,
                tenantId,
                createdBy,
                Instant.now()
        );
    }
}