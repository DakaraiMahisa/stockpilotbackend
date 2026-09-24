package com.stockpilot.backend.catalog.controller;

import com.stockpilot.backend.catalog.dto.request.CreatePriceListRequest;
import com.stockpilot.backend.catalog.dto.request.PriceListItemRequest;
import com.stockpilot.backend.catalog.dto.request.UpdatePriceListRequest;
import com.stockpilot.backend.catalog.dto.response.PriceListDto;
import com.stockpilot.backend.catalog.dto.response.PriceListItemDto;
import com.stockpilot.backend.catalog.service.PriceListService;
import com.stockpilot.backend.shared.api.ApiResponse;
import com.stockpilot.backend.shared.api.ApiRoutes;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.PRICE_LISTS)
@RequiredArgsConstructor
public class PriceListController {

    private final PriceListService priceListService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @PostMapping
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.CatalogPricingPermissions).CREATE)"
    )
    public ResponseEntity<ApiResponse<PriceListDto>> create(
            @Valid @RequestBody CreatePriceListRequest request
    ) {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        PriceListDto response =
                priceListService.create(tenantId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                response,
                                "Price list created successfully."
                        )
                );
    }

    @GetMapping
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.CatalogPricingPermissions).READ)"
    )
    public ResponseEntity<ApiResponse<Page<PriceListDto>>> getAll(
            Pageable pageable
    ) {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Page<PriceListDto> response =
                priceListService.getAll(tenantId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Price lists retrieved successfully."
                )
        );
    }

    @GetMapping("/{priceListId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.CatalogPricingPermissions).READ)"
    )
    public ResponseEntity<ApiResponse<PriceListDto>> getById(
            @PathVariable UUID priceListId
    ) {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        PriceListDto response =
                priceListService.getById(
                        tenantId,
                        priceListId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Price list retrieved successfully."
                )
        );
    }

    @PutMapping("/{priceListId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.CatalogPricingPermissions).UPDATE)"
    )
    public ResponseEntity<ApiResponse<PriceListDto>> update(
            @PathVariable UUID priceListId,
            @Valid @RequestBody UpdatePriceListRequest request
    ) {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        PriceListDto response =
                priceListService.update(
                        tenantId,
                        priceListId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Price list updated successfully."
                )
        );
    }

    @DeleteMapping("/{priceListId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.CatalogPricingPermissions).DELETE)"
    )
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID priceListId
    ) {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        priceListService.delete(
                tenantId,
                priceListId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Price list deleted successfully."
                )
        );
    }

    @GetMapping("/{priceListId}/items")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.CatalogPricingPermissions).READ)"
    )
    public ResponseEntity<ApiResponse<List<PriceListItemDto>>> getItems(
            @PathVariable UUID priceListId
    ) {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        List<PriceListItemDto> response =
                priceListService.getItems(
                        tenantId,
                        priceListId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Price list items retrieved successfully."
                )
        );
    }

    @PutMapping("/{priceListId}/items")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.CatalogPricingPermissions).UPDATE)"
    )
    public ResponseEntity<ApiResponse<List<PriceListItemDto>>> upsertItems(
            @PathVariable UUID priceListId,
            @Valid @RequestBody List<@Valid PriceListItemRequest> requests
    ) {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        List<PriceListItemDto> response =
                priceListService.upsertItems(
                        tenantId,
                        priceListId,
                        requests
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Price list items updated successfully."
                )
        );
    }

    @DeleteMapping("/{priceListId}/items/{itemId}")
    @PreAuthorize("hasAuthority('catalog-pricing:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @PathVariable UUID priceListId,
            @PathVariable UUID itemId
    ) {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        priceListService.deleteItem(
                tenantId,
                priceListId,
                itemId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Price list item deleted successfully."
                )
        );
    }
}