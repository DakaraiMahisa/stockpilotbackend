package com.stockpilot.backend.inventory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WarehouseUpdateRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 255)
        String addressLine,

        Boolean defaultWarehouse
) {
}