package com.stockpilot.backend.inventory.controller;


import com.stockpilot.backend.shared.api.ApiResponse;
import com.stockpilot.backend.shared.api.ApiRoutes;
import com.stockpilot.backend.inventory.dto.request.BinLocationCreateRequest;
import com.stockpilot.backend.inventory.dto.response.BinLocationResponse;
import com.stockpilot.backend.inventory.dto.request.BinLocationUpdateRequest;
import com.stockpilot.backend.inventory.service.BinLocationService;
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
public class BinLocationController {

    private final BinLocationService binLocationService;

    @GetMapping("/{warehouseId}/bins")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.inventory.permissions.InventoryPermissions).BIN_READ)"
    )
    public ResponseEntity<ApiResponse<Page<BinLocationResponse>>> getBins(
            @PathVariable UUID warehouseId,
            @RequestParam(required = false) UUID parentBinId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        binLocationService.getBins(
                                warehouseId,
                                parentBinId,
                                pageable
                        ),
                        "Bin locations fetched successfully."
                )
        );
    }

    @PostMapping("/{warehouseId}/bins")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.inventory.permissions.InventoryPermissions).BIN_WRITE)"
    )
    public ResponseEntity<ApiResponse<BinLocationResponse>> createBin(
            @PathVariable UUID warehouseId,
            @Valid @RequestBody BinLocationCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                binLocationService.createBin(warehouseId, request),
                                "Bin location created successfully."
                        )
                );
    }

    @PatchMapping("/{warehouseId}/bins/{binId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.inventory.permissions.InventoryPermissions).BIN_WRITE)"
    )
    public ResponseEntity<ApiResponse<BinLocationResponse>> updateBin(
            @PathVariable UUID warehouseId,
            @PathVariable UUID binId,
            @Valid @RequestBody BinLocationUpdateRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        binLocationService.updateBin(
                                warehouseId,
                                binId,
                                request
                        ),
                        "Bin location updated successfully."
                )
        );
    }
}
