package com.stockpilot.backend.catalog.event;

import java.time.Instant;
import java.util.UUID;

public record CategoryMovedEvent(

        UUID categoryId,

        UUID tenantId,

        UUID movedBy,

        Instant occurredAt

) {

    public static CategoryMovedEvent of(
            UUID categoryId,
            UUID tenantId,
            UUID movedBy
    ) {
        return new CategoryMovedEvent(
                categoryId,
                tenantId,
                movedBy,
                Instant.now()
        );
    }
}