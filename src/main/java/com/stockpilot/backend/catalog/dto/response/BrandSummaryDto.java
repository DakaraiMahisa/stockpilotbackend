package com.stockpilot.backend.catalog.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record BrandSummaryDto(

        UUID id,

        String name,

        String code
) {
}
