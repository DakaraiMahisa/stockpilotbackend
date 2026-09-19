package com.stockpilot.backend.catalog.dto.request;

import com.stockpilot.backend.catalog.enums.PriceListType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdatePriceListRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @NotNull
        PriceListType priceListType,

        LocalDate validFrom,

        LocalDate validTo,

        boolean defaultList,

        boolean active

) {
}
