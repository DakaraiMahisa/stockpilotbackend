package com.stockpilot.backend.inventory.dto.request;

import com.stockpilot.backend.inventory.enums.BinType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record BinLocationCreateRequest(

        UUID parentBinId,

        @NotBlank
        @Size(max = 30)
        String code,

        @Size(max = 100)
        String name,

        @NotNull
        BinType binType
) {
}