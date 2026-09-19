package com.stockpilot.backend.catalog.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record VariantAttributeDto(

        UUID id,

        String name,

        String code,

        String description,

        boolean active,

        List<VariantAttributeValueDto> values

) {
}
