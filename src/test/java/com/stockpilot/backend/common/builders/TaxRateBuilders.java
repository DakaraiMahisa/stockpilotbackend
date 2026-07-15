package com.stockpilot.backend.common.builders;

import com.stockpilot.backend.common.utils.TestConstants;
import com.stockpilot.backend.org.dto.request.CreateTaxRateRequest;
import com.stockpilot.backend.org.dto.response.TaxRateDto;
import com.stockpilot.backend.org.entity.TaxRate;
import com.stockpilot.backend.org.enums.RateType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class TaxRateBuilders {

    private TaxRateBuilders() {
    }

    public static TaxRate.TaxRateBuilder aTaxRate() {
        return TaxRate.builder()
                .id(TestConstants.TAX_CLASS_ID)
                .tenantId(TestConstants.TENANT_ID)
                .rateType(RateType.CGST)
                .rate(new BigDecimal("9.000"))
                .effectiveFrom(LocalDate.now().plusDays(1));
    }

    public static CreateTaxRateRequest createCgstRateRequest() {
        return new CreateTaxRateRequest(
                RateType.CGST,
                new BigDecimal("9.000"),
                LocalDate.now().plusDays(1)
        );
    }

    public static CreateTaxRateRequest createSgstRateRequest() {
        return new CreateTaxRateRequest(
                RateType.SGST,
                new BigDecimal("9.000"),
                LocalDate.now().plusDays(1)
        );
    }

    public static TaxRateDto taxRateDto() {
        return new TaxRateDto(
                UUID.randomUUID(),
                RateType.CGST,
                new BigDecimal("9.000"),
                LocalDate.now().plusDays(1),
                null
        );
    }
}