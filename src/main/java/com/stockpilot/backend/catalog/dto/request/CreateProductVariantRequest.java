package com.stockpilot.backend.catalog.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record CreateProductVariantRequest(

        @NotNull
        Map<String, String> attributes,

        @NotBlank
        @Size(max = 60)
        String barcode,

        @NotNull
        @DecimalMin(value = "0.0")
        BigDecimal costPrice,

        @NotNull
        @DecimalMin(value = "0.0")
        BigDecimal retailPrice,

        @Size(max = 500)
        String imageUrl,

        @DecimalMin(value = "0.0")
        BigDecimal additionalWeightKg

) {
}