package com.stockpilot.backend.inventory.dto.response;

import com.stockpilot.backend.inventory.enums.WarehouseStatus;

import java.time.Instant;
import java.util.UUID;

public record WarehouseResponse(
        UUID id,
        UUID branchId,
        String name,
        String code,
        String addressLine,
        boolean defaultWarehouse,
        boolean binTrackingEnabled,
        WarehouseStatus status,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {
}
