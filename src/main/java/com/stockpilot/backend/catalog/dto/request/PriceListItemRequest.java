package com.stockpilot.backend.catalog.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PriceListItemRequest(

        UUID productId,

        UUID variantId,

        @NotNull
        @DecimalMin(value = "0.001")
        @Digits(integer = 9, fraction = 3)
        BigDecimal minQuantity,

        @NotNull
        @DecimalMin(value = "0.00")
        @Digits(integer = 10, fraction = 4)
        BigDecimal unitPrice,

        @DecimalMin(value = "0.00")
        @DecimalMax(value = "100.00")
        @Digits(integer = 3, fraction = 2)
        BigDecimal discountPct

) {
}
