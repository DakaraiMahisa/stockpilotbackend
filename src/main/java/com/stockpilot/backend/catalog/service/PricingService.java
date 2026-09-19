package com.stockpilot.backend.catalog.service;

import com.stockpilot.backend.catalog.dto.request.PriceResolveRequest;
import com.stockpilot.backend.catalog.dto.response.PriceResolveResponse;

import java.util.UUID;

public interface PricingService {

    PriceResolveResponse resolvePrice(
            UUID tenantId,
            PriceResolveRequest request
    );
}