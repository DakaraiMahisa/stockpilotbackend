package com.stockpilot.backend.catalog.event;

import java.time.Instant;
import java.util.UUID;

public record CategoryDeletedEvent(

        UUID categoryId,

        UUID tenantId,

        UUID deletedBy,

        Instant occurredAt

) {

    public static CategoryDeletedEvent of(
            UUID categoryId,
            UUID tenantId,
            UUID deletedBy
    ) {
        return new CategoryDeletedEvent(
                categoryId,
                tenantId,
                deletedBy,
                Instant.now()
        );
    }
}