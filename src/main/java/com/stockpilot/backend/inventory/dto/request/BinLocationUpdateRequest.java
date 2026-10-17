package com.stockpilot.backend.inventory.dto.request;

import com.stockpilot.backend.inventory.enums.BinType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BinLocationUpdateRequest(

        @NotBlank
        @Size(max = 30)
        String code,

        @Size(max = 100)
        String name,

        BinType binType,

        Boolean active
) {
}
