package com.stockpilot.backend.catalog.dto.response;

import com.stockpilot.backend.catalog.enums.PriceListType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PriceListDto(

        UUID id,

        String name,

        String code,

        PriceListType priceListType,

        String currencyCode,

        boolean defaultList,

        LocalDate validFrom,

        LocalDate validTo,

        boolean active,

        Instant createdAt,

        Instant updatedAt

) {
}