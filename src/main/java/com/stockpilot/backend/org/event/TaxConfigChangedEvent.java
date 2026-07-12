package com.stockpilot.backend.org.event;


import java.time.Instant;
import java.util.UUID;

public record TaxConfigChangedEvent(

        UUID taxClassId,

        UUID tenantId,

        UUID changedBy,

        Instant occurredAt

) {

    public static TaxConfigChangedEvent of(
            UUID taxClassId,
            UUID tenantId,
            UUID changedBy
    ) {
        return new TaxConfigChangedEvent(
                taxClassId,
                tenantId,
                changedBy,
                Instant.now()
        );
    }
}