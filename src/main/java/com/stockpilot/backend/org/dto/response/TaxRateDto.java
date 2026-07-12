package com.stockpilot.backend.org.dto.response;

import com.stockpilot.backend.org.enums.RateType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TaxRateDto(

        UUID id,

        RateType rateType,

        BigDecimal rate,

        LocalDate effectiveFrom,

        LocalDate effectiveTo

) {
}