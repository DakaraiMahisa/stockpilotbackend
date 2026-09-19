package com.stockpilot.backend.catalog.controller;

import com.stockpilot.backend.catalog.dto.request.CreateProductVariantRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateProductVariantRequest;
import com.stockpilot.backend.catalog.dto.response.ProductVariantDto;
import com.stockpilot.backend.catalog.service.ProductVariantService;
import com.stockpilot.backend.shared.api.ApiResponse;
import com.stockpilot.backend.shared.api.ApiMessages;
import com.stockpilot.backend.shared.api.ApiRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.PRODUCTS)
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    @PostMapping("/{productId}/variants")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).CREATE)"
    )
    public ResponseEntity<ApiResponse<List<ProductVariantDto>>> createVariants(
            @PathVariable UUID productId,
            @Valid @RequestBody List<CreateProductVariantRequest> requests
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                productVariantService.createVariants(
                                        productId,
                                        requests
                                ),
                                ApiMessages.PRODUCT_VARIANTS_CREATED
                        )
                );
    }

    @GetMapping("/{productId}/variants")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).READ)"
    )
    public ResponseEntity<ApiResponse<List<ProductVariantDto>>> getVariantsByProductId(
            @PathVariable UUID productId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productVariantService.getVariantsByProductId(productId),
                        ApiMessages.PRODUCT_VARIANTS_FETCHED
                )
        );
    }

    @GetMapping("/{productId}/variants/{variantId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).READ)"
    )
    public ResponseEntity<ApiResponse<ProductVariantDto>> getVariantById(
            @PathVariable UUID productId,
            @PathVariable UUID variantId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productVariantService.getVariantById(
                                productId,
                                variantId
                        ),
                        ApiMessages.PRODUCT_VARIANT_FETCHED
                )
        );
    }

    @PutMapping("/{productId}/variants/{variantId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).UPDATE)"
    )
    public ResponseEntity<ApiResponse<ProductVariantDto>> updateVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId,
            @Valid @RequestBody UpdateProductVariantRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productVariantService.updateVariant(
                                productId,
                                variantId,
                                request
                        ),
                        ApiMessages.PRODUCT_VARIANT_UPDATED
                )
        );
    }

    @PatchMapping("/{productId}/variants/{variantId}/deactivate")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).UPDATE)"
    )
    public ResponseEntity<ApiResponse<Void>> deactivateVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId
    ) {

        productVariantService.deactivateVariant(
                productId,
                variantId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        ApiMessages.PRODUCT_VARIANT_DEACTIVATED
                )
        );
    }

    @DeleteMapping("/{productId}/variants/{variantId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).DELETE)"
    )
    public ResponseEntity<ApiResponse<Void>> deleteVariant(
            @PathVariable UUID productId,
            @PathVariable UUID variantId
    ) {

        productVariantService.deleteVariant(
                productId,
                variantId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        ApiMessages.PRODUCT_VARIANT_DELETED
                )
        );
    }
}