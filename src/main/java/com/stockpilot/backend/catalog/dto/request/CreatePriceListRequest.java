package com.stockpilot.backend.catalog.dto.request;

import com.stockpilot.backend.catalog.enums.PriceListType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreatePriceListRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 20)
        String code,

        @NotNull
        PriceListType priceListType,

        LocalDate validFrom,

        LocalDate validTo,

        boolean defaultList,

        boolean active

) {
}
