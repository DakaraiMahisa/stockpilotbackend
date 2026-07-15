package com.stockpilot.backend.common.builders;

import com.stockpilot.backend.org.entity.TaxClass;
import com.stockpilot.backend.org.entity.TaxRate;
import com.stockpilot.backend.org.enums.TaxType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Test builder for {@link TaxClass} to simplify test setup.
 */
public final class TaxClassBuilder {

    private UUID id = null;
    private String name = "Standard";
    private String code = "STD";
    private TaxType taxType = TaxType.VAT;
    private boolean defaultTaxClass = false;
    private String hsnSacCode = null;
    private String description = null;
    private List<TaxRate> rates = new ArrayList<>();

    private TaxClassBuilder() {}

    public static TaxClassBuilder aTaxClass() {
        return new TaxClassBuilder();
    }

    public TaxClassBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public TaxClassBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public TaxClassBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public TaxClassBuilder withTaxType(TaxType taxType) {
        this.taxType = taxType;
        return this;
    }

    public TaxClassBuilder withRates(List<TaxRate> rates) {
        this.rates = new ArrayList<>(rates);
        return this;
    }

    public TaxClassBuilder addRate(TaxRate rate) {
        this.rates.add(rate);
        return this;
    }

    public TaxClass build() {
        TaxClass.TaxClassBuilder builder = TaxClass.builder()
                .name(name)
                .code(code)
                .taxType(taxType)
                .defaultTaxClass(defaultTaxClass)
                .hsnSacCode(hsnSacCode)
                .description(description);

        if (id != null) {
            builder.id(id);
        }

        TaxClass tc = builder.build();


        if (rates != null && !rates.isEmpty()) {
            tc.setRates(new ArrayList<>(rates));
            tc.getRates().forEach(r -> r.setTaxClass(tc));
        }

        return tc;
    }
}

