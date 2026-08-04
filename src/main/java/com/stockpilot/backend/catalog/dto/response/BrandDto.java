package com.stockpilot.backend.catalog.dto.response;


import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record BrandDto(

        UUID id,

        String name,

        String code,

        String logoObjectKey,

        String website,

        boolean active,

        Instant createdAt,

        Instant updatedAt
) {
}
