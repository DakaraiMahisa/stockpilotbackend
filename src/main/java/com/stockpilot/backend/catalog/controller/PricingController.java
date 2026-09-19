package com.stockpilot.backend.catalog.controller;

import com.stockpilot.backend.catalog.dto.request.PriceResolveRequest;
import com.stockpilot.backend.catalog.dto.response.PriceResolveResponse;
import com.stockpilot.backend.catalog.service.PricingService;
import com.stockpilot.backend.shared.api.ApiResponse;
import com.stockpilot.backend.shared.api.ApiRoutes;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.PRICING)
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @PostMapping("/resolve")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.CatalogPricingPermissions).READ)"
    )
    public ResponseEntity<ApiResponse<PriceResolveResponse>> resolvePrice(
            @Valid @RequestBody PriceResolveRequest request
    ) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        PriceResolveResponse response =
                pricingService.resolvePrice(tenantId, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Price resolved successfully."
                )
        );
    }
}