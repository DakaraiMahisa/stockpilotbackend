package com.stockpilot.backend.catalog.controller;


import com.stockpilot.backend.catalog.dto.request.CreateVariantAttributeRequest;
import com.stockpilot.backend.catalog.dto.request.CreateVariantAttributeValueRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateVariantAttributeRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateVariantAttributeValueRequest;
import com.stockpilot.backend.catalog.dto.response.VariantAttributeDto;
import com.stockpilot.backend.catalog.dto.response.VariantAttributeValueDto;
import com.stockpilot.backend.catalog.service.VariantAttributeService;
import com.stockpilot.backend.shared.api.ApiMessages;
import com.stockpilot.backend.shared.api.ApiResponse;
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
@RequestMapping(ApiRoutes.VARIANT_ATTRIBUTES)
public class VariantAttributeController {

    private final VariantAttributeService variantAttributeService;


    @PostMapping
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).CREATE)"
    )
    public ResponseEntity<ApiResponse<VariantAttributeDto>> createAttribute(
            @Valid @RequestBody CreateVariantAttributeRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                variantAttributeService.createAttribute(request),
                                ApiMessages.VARIANT_ATTRIBUTE_CREATED
                        )
                );
    }

    @GetMapping
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).READ)"
    )
    public ResponseEntity<ApiResponse<List<VariantAttributeDto>>> getAllAttributes() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        variantAttributeService.getAllAttributes(),
                        ApiMessages.VARIANT_ATTRIBUTES_FETCHED
                )
        );
    }

    @GetMapping("/{attributeId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).READ)"
    )
    public ResponseEntity<ApiResponse<VariantAttributeDto>> getAttributeById(
            @PathVariable UUID attributeId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        variantAttributeService.getAttributeById(attributeId),
                        ApiMessages.VARIANT_ATTRIBUTE_FETCHED
                )
        );
    }

    @PutMapping("/{attributeId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).UPDATE)"
    )
    public ResponseEntity<ApiResponse<VariantAttributeDto>> updateAttribute(
            @PathVariable UUID attributeId,
            @Valid @RequestBody UpdateVariantAttributeRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        variantAttributeService.updateAttribute(
                                attributeId,
                                request
                        ),
                        ApiMessages.VARIANT_ATTRIBUTE_UPDATED
                )
        );
    }

    @PatchMapping("/{attributeId}/activate")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).UPDATE)"
    )
    public ResponseEntity<ApiResponse<Void>> activateAttribute(
            @PathVariable UUID attributeId
    ) {
        variantAttributeService.activateAttribute(attributeId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        ApiMessages.VARIANT_ATTRIBUTE_ACTIVATED
                )
        );
    }

    @PatchMapping("/{attributeId}/deactivate")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).UPDATE)"
    )
    public ResponseEntity<ApiResponse<Void>> deactivateAttribute(
            @PathVariable UUID attributeId
    ) {
        variantAttributeService.deactivateAttribute(attributeId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        ApiMessages.VARIANT_ATTRIBUTE_DEACTIVATED
                )
        );
    }

    @DeleteMapping("/{attributeId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).DELETE)"
    )
    public ResponseEntity<ApiResponse<Void>> deleteAttribute(
            @PathVariable UUID attributeId
    ) {
        variantAttributeService.deleteAttribute(attributeId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        ApiMessages.VARIANT_ATTRIBUTE_DELETED
                )
        );
    }


    @PostMapping("/{attributeId}/values")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).CREATE)"
    )
    public ResponseEntity<ApiResponse<VariantAttributeValueDto>> createValue(
            @PathVariable UUID attributeId,
            @Valid @RequestBody CreateVariantAttributeValueRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                variantAttributeService.createValue(
                                        attributeId,
                                        request
                                ),
                                ApiMessages.VARIANT_ATTRIBUTE_VALUE_CREATED
                        )
                );
    }

    @GetMapping("/{attributeId}/values")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).READ)"
    )
    public ResponseEntity<ApiResponse<List<VariantAttributeValueDto>>> getValues(
            @PathVariable UUID attributeId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        variantAttributeService.getValues(attributeId),
                        ApiMessages.VARIANT_ATTRIBUTE_VALUES_FETCHED
                )
        );
    }

    @GetMapping("/{attributeId}/values/{valueId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).READ)"
    )
    public ResponseEntity<ApiResponse<VariantAttributeValueDto>> getValueById(
            @PathVariable UUID attributeId,
            @PathVariable UUID valueId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        variantAttributeService.getValueById(
                                attributeId,
                                valueId
                        ),
                        ApiMessages.VARIANT_ATTRIBUTE_VALUE_FETCHED
                )
        );
    }

    @PutMapping("/{attributeId}/values/{valueId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).UPDATE)"
    )
    public ResponseEntity<ApiResponse<VariantAttributeValueDto>> updateValue(
            @PathVariable UUID attributeId,
            @PathVariable UUID valueId,
            @Valid @RequestBody UpdateVariantAttributeValueRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        variantAttributeService.updateValue(
                                attributeId,
                                valueId,
                                request
                        ),
                        ApiMessages.VARIANT_ATTRIBUTE_VALUE_UPDATED
                )
        );
    }

    @PatchMapping("/{attributeId}/values/{valueId}/activate")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).UPDATE)"
    )
    public ResponseEntity<ApiResponse<Void>> activateValue(
            @PathVariable UUID attributeId,
            @PathVariable UUID valueId
    ) {
        variantAttributeService.activateValue(
                attributeId,
                valueId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        ApiMessages.VARIANT_ATTRIBUTE_VALUE_ACTIVATED
                )
        );
    }

    @PatchMapping("/{attributeId}/values/{valueId}/deactivate")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).UPDATE)"
    )
    public ResponseEntity<ApiResponse<Void>> deactivateValue(
            @PathVariable UUID attributeId,
            @PathVariable UUID valueId
    ) {
        variantAttributeService.deactivateValue(
                attributeId,
                valueId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        ApiMessages.VARIANT_ATTRIBUTE_VALUE_DEACTIVATED
                )
        );
    }

    @DeleteMapping("/{attributeId}/values/{valueId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductVariantPermissions).DELETE)"
    )
    public ResponseEntity<ApiResponse<Void>> deleteValue(
            @PathVariable UUID attributeId,
            @PathVariable UUID valueId
    ) {
        variantAttributeService.deleteValue(
                attributeId,
                valueId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        ApiMessages.VARIANT_ATTRIBUTE_VALUE_DELETED
                )
        );
    }
}
