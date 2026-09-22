package com.stockpilot.backend.catalog.dto.response;

import com.stockpilot.backend.catalog.enums.UnitOfMeasure;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(

        UUID id,

        String sku,

        String barcode,

        String name,

        String description,

        UUID categoryId,

        String categoryName,

        UUID brandId,

        String brandName,

        UUID taxClassId,

        String taxClassName,

        UnitOfMeasure unitOfMeasure,

        Boolean trackBatches,

        Integer minStockLevel,

        Integer maxStockLevel,

        Boolean service,

        BigDecimal weightKg,

        BigDecimal retailPrice,

        BigDecimal stockQty,

        boolean active

) {
}