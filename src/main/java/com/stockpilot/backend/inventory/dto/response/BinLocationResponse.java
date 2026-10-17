package com.stockpilot.backend.inventory.dto.response;

import com.stockpilot.backend.inventory.enums.BinType;

import java.time.Instant;
import java.util.UUID;

public record BinLocationResponse(
        UUID id,
        UUID warehouseId,
        UUID parentBinId,
        String code,
        String name,
        BinType binType,
        boolean active,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {
}
