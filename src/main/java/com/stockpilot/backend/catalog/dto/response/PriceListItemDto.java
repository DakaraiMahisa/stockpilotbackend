package com.stockpilot.backend.catalog.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record PriceListItemDto(

        UUID id,

        UUID priceListId,

        UUID productId,

        UUID variantId,

        BigDecimal minQuantity,

        BigDecimal unitPrice,

        BigDecimal discountPct

) {
}
