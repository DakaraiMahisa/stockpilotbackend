package com.stockpilot.backend.org.event;


import java.time.Instant;
import java.util.UUID;

public record OrganizationProfileUpdatedEvent(

        UUID organizationId,

        UUID tenantId,

        UUID updatedBy,

        Instant occurredAt

) {

    public static OrganizationProfileUpdatedEvent of(
            UUID organizationId,
            UUID tenantId,
            UUID updatedBy
    ) {
        return new OrganizationProfileUpdatedEvent(
                organizationId,
                tenantId,
                updatedBy,
                Instant.now()
        );
    }
}
