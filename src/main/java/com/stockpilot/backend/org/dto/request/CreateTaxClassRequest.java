package com.stockpilot.backend.org.dto.request;

import com.stockpilot.backend.org.enums.TaxType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateTaxClassRequest(

        @NotBlank
        @Size(max = 80)
        String name,

        @NotBlank
        @Size(max = 20)
        String code,

        TaxType taxType,

        Boolean isDefault,

        @Size(max = 10)
        String hsnSacCode,

        @Size(max = 1000)
        String description,

        @Valid
        @NotEmpty
        List<CreateTaxRateRequest> rates

) {
}