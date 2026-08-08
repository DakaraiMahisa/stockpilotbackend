package com.stockpilot.backend.catalog.dto.response;

import com.stockpilot.backend.catalog.enums.UnitOfMeasure;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(

        UUID id,

        String sku,

        String barcode,

        String name,

        UUID categoryId,

        String categoryName,

        UUID brandId,

        String brandName,

        UnitOfMeasure unitOfMeasure,

        BigDecimal retailPrice,

        BigDecimal stockQty,

        boolean active

) {
}