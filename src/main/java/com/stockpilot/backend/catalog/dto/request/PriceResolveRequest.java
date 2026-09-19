package com.stockpilot.backend.catalog.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PriceResolveRequest(

        UUID productId,

        UUID variantId,

        @NotNull
        @DecimalMin(value = "0.001", inclusive = true)
        @Digits(integer = 9, fraction = 3)
        BigDecimal quantity,

        LocalDate date

) {
}