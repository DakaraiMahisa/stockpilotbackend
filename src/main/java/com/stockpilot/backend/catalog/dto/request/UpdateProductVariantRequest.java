package com.stockpilot.backend.catalog.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record UpdateProductVariantRequest(

        String barcode,

        @DecimalMin(value = "0.0")
        BigDecimal costPrice,

        @DecimalMin(value = "0.0")
        BigDecimal retailPrice,

        @Size(max = 500)
        String imageUrl,

        @DecimalMin(value = "0.0")
        BigDecimal additionalWeightKg,

        Boolean active

) {
}