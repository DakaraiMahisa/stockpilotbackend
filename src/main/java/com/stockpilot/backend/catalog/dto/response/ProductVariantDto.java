package com.stockpilot.backend.catalog.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Builder
public record ProductVariantDto(

        UUID id,

        UUID productId,

        String sku,

        String barcode,

        Map<String, String> attributes,

        BigDecimal costPrice,

        BigDecimal retailPrice,

        String imageUrl,

        boolean active,

        BigDecimal additionalWeightKg

) {
}
