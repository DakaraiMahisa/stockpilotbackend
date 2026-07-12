package com.stockpilot.backend.org.dto.request;

import com.stockpilot.backend.org.enums.RateType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AddTaxRateRequest(

        @NotNull
        RateType rateType,

        @NotNull
        @DecimalMin("0.000")
        @DecimalMax("100.000")
        BigDecimal rate,

        @NotNull
        LocalDate effectiveFrom

) {
}
