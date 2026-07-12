package com.stockpilot.backend.org.dto.response;

import com.stockpilot.backend.org.enums.TaxType;

import java.util.List;
import java.util.UUID;

public record TaxClassDto(

        UUID id,

        String name,

        String code,

        TaxType taxType,

        boolean defaultTaxClass,

        String hsnSacCode,

        String description,

        List<TaxRateDto> rates

) {
}
