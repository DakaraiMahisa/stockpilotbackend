package com.stockpilot.backend.inventory.controller;

import com.stockpilot.backend.inventory.dto.request.WarehouseCreateRequest;
import com.stockpilot.backend.inventory.dto.request.WarehouseUpdateRequest;
import com.stockpilot.backend.inventory.dto.response.WarehouseResponse;
import com.stockpilot.backend.inventory.service.WarehouseService;
import com.stockpilot.backend.shared.api.ApiResponse;
import com.stockpilot.backend.shared.api.ApiRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.WAREHOUSES)
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.inventory.permissions.InventoryPermissions).WAREHOUSE_READ)"
    )
    public ResponseEntity<ApiResponse<Page<WarehouseResponse>>> getWarehouses(
            @RequestParam(required = false) UUID branchId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        warehouseService.getWarehouses(branchId, pageable),
                        "Warehouses fetched successfully."
                )
        );
    }

    @GetMapping("/{warehouseId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.inventory.permissions.InventoryPermissions).WAREHOUSE_READ)"
    )
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouseById(
            @PathVariable UUID warehouseId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        warehouseService.getWarehouseById(warehouseId),
                        "Warehouse fetched successfully."
                )
        );
    }

    @PostMapping
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.inventory.permissions.InventoryPermissions).WAREHOUSE_WRITE)"
    )
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(
            @Valid @RequestBody WarehouseCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                warehouseService.createWarehouse(request),
                                "Warehouse created successfully."
                        )
                );
    }

    @PatchMapping("/{warehouseId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.inventory.permissions.InventoryPermissions).WAREHOUSE_WRITE)"
    )
    public ResponseEntity<ApiResponse<WarehouseResponse>> updateWarehouse(
            @PathVariable UUID warehouseId,
            @Valid @RequestBody WarehouseUpdateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        warehouseService.updateWarehouse(warehouseId, request),
                        "Warehouse updated successfully."
                )
        );
    }

    @PatchMapping("/{warehouseId}/deactivate")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.inventory.permissions.InventoryPermissions).WAREHOUSE_WRITE)"
    )
    public ResponseEntity<ApiResponse<Void>> deactivateWarehouse(
            @PathVariable UUID warehouseId
    ) {
        warehouseService.deactivateWarehouse(warehouseId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Warehouse deactivated successfully."
                )
        );
    }

    @PatchMapping("/{warehouseId}/activate")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.inventory.permissions.InventoryPermissions).WAREHOUSE_WRITE)"
    )
    public ResponseEntity<ApiResponse<Void>> activateWarehouse(
            @PathVariable UUID warehouseId
    ) {
        warehouseService.activateWarehouse(warehouseId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Warehouse activated successfully."
                )
        );
    }
}