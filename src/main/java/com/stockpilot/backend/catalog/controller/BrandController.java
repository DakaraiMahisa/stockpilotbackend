package com.stockpilot.backend.catalog.controller;

import com.stockpilot.backend.catalog.dto.request.BrandCreateRequest;
import com.stockpilot.backend.catalog.dto.request.BrandUpdateRequest;
import com.stockpilot.backend.catalog.dto.response.BrandDto;
import com.stockpilot.backend.catalog.dto.response.BrandSummaryDto;
import com.stockpilot.backend.catalog.service.BrandService;
import com.stockpilot.backend.shared.api.ApiResponse;
import com.stockpilot.backend.shared.api.ApiMessages;
import com.stockpilot.backend.shared.api.ApiRoutes;
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
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.BRANDS)
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.catalog.permissions.BrandPermissions).CREATE)")
    public ResponseEntity<ApiResponse<BrandDto>> createBrand(
            @Valid @RequestBody BrandCreateRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                brandService.createBrand(request),
                                ApiMessages.BRAND_CREATED
                        )
                );
    }

    @PutMapping("/{brandId}")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.catalog.permissions.BrandPermissions).UPDATE)")
    public ResponseEntity<ApiResponse<BrandDto>> updateBrand(
            @PathVariable UUID brandId,
            @Valid @RequestBody BrandUpdateRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        brandService.updateBrand(brandId, request),
                        ApiMessages.BRAND_UPDATED
                )
        );
    }

    @GetMapping("/{brandId}")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.catalog.permissions.BrandPermissions).READ)")
    public ResponseEntity<ApiResponse<BrandDto>> getBrandById(
            @PathVariable UUID brandId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        brandService.getBrandById(brandId)
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.catalog.permissions.BrandPermissions).READ)")
    public ResponseEntity<ApiResponse<Page<BrandDto>>> getBrands(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        brandService.getBrands(search, active, pageable)
                )
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.catalog.permissions.BrandPermissions).READ)")
    public ResponseEntity<ApiResponse<List<BrandSummaryDto>>> getActiveBrands() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        brandService.getActiveBrands()
                )
        );
    }

    @DeleteMapping("/{brandId}")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.catalog.permissions.BrandPermissions).DELETE)")
    public ResponseEntity<ApiResponse<Void>> deactivateBrand(
            @PathVariable UUID brandId
    ) {

        brandService.deactivateBrand(brandId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        ApiMessages.BRAND_DEACTIVATED
                )
        );
    }

    @PatchMapping("/{brandId}/activate")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.catalog.permissions.BrandPermissions).UPDATE)")
    public ResponseEntity<ApiResponse<BrandDto>> activateBrand(
            @PathVariable UUID brandId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        brandService.activateBrand(brandId),
                        ApiMessages.BRAND_ACTIVATED
                )
        );
    }
}