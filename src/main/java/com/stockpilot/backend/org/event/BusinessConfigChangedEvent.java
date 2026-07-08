package com.stockpilot.backend.org.event;

import java.time.Instant;
import java.util.UUID;

public record BusinessConfigChangedEvent(

        UUID businessConfigId,

        UUID tenantId,

        UUID updatedBy,

        Instant occurredAt

) {

    public static BusinessConfigChangedEvent of(
            UUID businessConfigId,
            UUID tenantId,
            UUID updatedBy
    ) {
        return new BusinessConfigChangedEvent(
                businessConfigId,
                tenantId,
                updatedBy,
                Instant.now()
        );
    }
}