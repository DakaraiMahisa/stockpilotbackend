package com.stockpilot.backend.catalog.event;

import lombok.Builder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Builder
public record ProductCreatedEvent(

        UUID productId,

        UUID tenantId,

        String sku,

        String name,

        Instant occurredAt

) implements Serializable {
}