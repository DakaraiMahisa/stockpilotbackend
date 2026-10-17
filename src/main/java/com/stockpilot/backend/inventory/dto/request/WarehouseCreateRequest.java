package com.stockpilot.backend.inventory.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record WarehouseCreateRequest(

        @NotNull
        UUID branchId,

        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 20)
        String code,

        @Size(max = 255)
        String addressLine,

        boolean defaultWarehouse,

        boolean binTrackingEnabled
) {
}