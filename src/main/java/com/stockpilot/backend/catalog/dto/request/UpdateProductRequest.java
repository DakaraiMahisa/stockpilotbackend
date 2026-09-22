package com.stockpilot.backend.catalog.dto.request;

import com.stockpilot.backend.catalog.enums.UnitOfMeasure;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;
@Builder
public record UpdateProductRequest(

        @Size(max = 60)
        String barcode,

        @Size(max = 200)
        String name,

        @Size(max = 5000)
        String description,

        UUID brandId,

        UUID categoryId,

        UUID taxClassId,

        UnitOfMeasure unitOfMeasure,

        Boolean trackBatches,

        @Min(0)
        Integer minStockLevel,

        @Min(0)
        Integer maxStockLevel,

        Boolean active,

        Boolean service,

        @DecimalMin(value = "0.0", inclusive = true)
        @Digits(integer = 5, fraction = 3)
        BigDecimal weightKg,

        @Size(max = 5000)
        String notes

) {
}