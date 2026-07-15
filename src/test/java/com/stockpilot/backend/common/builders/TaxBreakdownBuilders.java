package com.stockpilot.backend.common.builders;

import com.stockpilot.backend.org.dto.response.TaxBreakdownDto;

import java.math.BigDecimal;

public final class TaxBreakdownBuilders {

    private TaxBreakdownBuilders() {
    }

    public static TaxBreakdownDto gstBreakdown() {
        return TaxBreakdownDto.builder()
                .taxableAmount(new BigDecimal("1000.00"))
                .cgst(new BigDecimal("90.00"))
                .sgst(new BigDecimal("90.00"))
                .igst(BigDecimal.ZERO)
                .vat(BigDecimal.ZERO)
                .totalTax(new BigDecimal("180.00"))
                .totalWithTax(new BigDecimal("1180.00"))
                .build();
    }

    public static TaxBreakdownDto vatBreakdown() {
        return TaxBreakdownDto.builder()
                .taxableAmount(new BigDecimal("1000.00"))
                .cgst(BigDecimal.ZERO)
                .sgst(BigDecimal.ZERO)
                .igst(BigDecimal.ZERO)
                .vat(new BigDecimal("150.00"))
                .totalTax(new BigDecimal("150.00"))
                .totalWithTax(new BigDecimal("1150.00"))
                .build();
    }

    public static TaxBreakdownDto noTaxBreakdown() {
        return TaxBreakdownDto.builder()
                .taxableAmount(new BigDecimal("1000.00"))
                .cgst(BigDecimal.ZERO)
                .sgst(BigDecimal.ZERO)
                .igst(BigDecimal.ZERO)
                .vat(BigDecimal.ZERO)
                .totalTax(BigDecimal.ZERO)
                .totalWithTax(new BigDecimal("1000.00"))
                .build();
    }
}