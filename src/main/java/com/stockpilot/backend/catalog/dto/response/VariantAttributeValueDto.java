package com.stockpilot.backend.catalog.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record VariantAttributeValueDto(

        UUID id,

        UUID attributeId,

        String value,

        String code,

        boolean active,

        Integer sortOrder

) {
}