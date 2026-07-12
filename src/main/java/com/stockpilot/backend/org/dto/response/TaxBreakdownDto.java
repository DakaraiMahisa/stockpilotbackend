package com.stockpilot.backend.org.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TaxBreakdownDto(

        BigDecimal taxableAmount,

        BigDecimal cgst,

        BigDecimal sgst,

        BigDecimal igst,

        BigDecimal vat,

        BigDecimal totalTax,

        BigDecimal totalWithTax

) {
}