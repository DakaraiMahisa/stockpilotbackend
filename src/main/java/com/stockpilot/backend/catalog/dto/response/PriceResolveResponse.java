package com.stockpilot.backend.catalog.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record PriceResolveResponse(

        UUID priceListId,

        String priceListCode,

        String currencyCode,

        UUID productId,

        UUID variantId,

        BigDecimal quantity,

        BigDecimal minQuantity,

        BigDecimal unitPrice,

        BigDecimal discountPct,

        BigDecimal effectiveUnitPrice

) {
}
