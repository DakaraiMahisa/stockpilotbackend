package com.stockpilot.backend.catalog.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record ProductUpdatedEvent(

        UUID productId,

        UUID tenantId,

        String sku,

        String name,

        Instant occurredAt

) implements Serializable {
}