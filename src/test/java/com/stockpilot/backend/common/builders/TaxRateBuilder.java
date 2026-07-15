package com.stockpilot.backend.common.builders;

import com.stockpilot.backend.org.entity.TaxClass;
import com.stockpilot.backend.org.entity.TaxRate;
import com.stockpilot.backend.org.enums.RateType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Test builder for {@link TaxRate} to simplify test setup.
 */
public final class TaxRateBuilder {

    private UUID id = null;
    private RateType rateType = RateType.VAT;
    private BigDecimal rate = BigDecimal.valueOf(18);
    private LocalDate effectiveFrom = LocalDate.now().minusDays(1);
    private LocalDate effectiveTo = null;
    private TaxClass taxClass = null;

    private TaxRateBuilder() {}

    public static TaxRateBuilder aTaxRate() {
        return new TaxRateBuilder();
    }

    public TaxRateBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public TaxRateBuilder withRateType(RateType rateType) {
        this.rateType = rateType;
        return this;
    }

    public TaxRateBuilder withRate(BigDecimal rate) {
        this.rate = rate;
        return this;
    }

    public TaxRateBuilder withEffectiveFrom(LocalDate from) {
        this.effectiveFrom = from;
        return this;
    }

    public TaxRateBuilder withEffectiveTo(LocalDate to) {
        this.effectiveTo = to;
        return this;
    }

    public TaxRateBuilder withTaxClass(TaxClass taxClass) {
        this.taxClass = taxClass;
        return this;
    }

    public TaxRate build() {
        TaxRate.TaxRateBuilder builder = TaxRate.builder()
                .rateType(rateType)
                .rate(rate)
                .effectiveFrom(effectiveFrom)
                .effectiveTo(effectiveTo);

        if (id != null) {
            builder.id(id);
        }

        TaxRate rateObj = builder.build();

        if (taxClass != null) {
            rateObj.setTaxClass(taxClass);
        }

        return rateObj;
    }
}

