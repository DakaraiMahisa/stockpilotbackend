package com.stockpilot.backend.catalog.dto.response;

import java.util.UUID;

public record ProductVariantAttributeDto(

        UUID id,

        UUID productId,

        UUID attributeId,

        String attributeName,

        String attributeCode,

        Integer sortOrder

) {
}