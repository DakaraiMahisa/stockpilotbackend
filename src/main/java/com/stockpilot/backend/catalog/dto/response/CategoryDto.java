package com.stockpilot.backend.catalog.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record CategoryDto(

        UUID id,

        String name,

        String code,

        String description,

        UUID parentId,

        Integer sortOrder,

        boolean active,

        Instant createdAt,

        Instant updatedAt
) {}