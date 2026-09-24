package com.stockpilot.backend.catalog.dto.request;

import com.stockpilot.backend.catalog.enums.UnitOfMeasure;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;
@Builder
public record CreateProductRequest(

        @Size(max = 50)
        @Pattern(
                regexp = "^$|^[A-Za-z0-9-]+$",
                message = "SKU may contain only letters, numbers and hyphens."
        )
        String sku,

        @Size(max = 60)
        String barcode,

        @NotBlank
        @Size(max = 200)
        String name,

        @Size(max = 5000)
        String description,

        UUID brandId,

        @NotNull
        UUID categoryId,

        @NotNull
        UUID taxClassId,

        @NotNull
        UnitOfMeasure unitOfMeasure,

        Boolean trackBatches,

        @Min(0)
        Integer minStockLevel,

        @Min(0)
        Integer maxStockLevel,

        Boolean service,

        @DecimalMin(value = "0.0", inclusive = true)
        @Digits(integer = 5, fraction = 3)
        BigDecimal weightKg,

        @Size(max = 5000)
        String notes

) {
}