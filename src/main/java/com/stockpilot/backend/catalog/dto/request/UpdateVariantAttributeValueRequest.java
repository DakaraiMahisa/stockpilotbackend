package com.stockpilot.backend.catalog.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record UpdateVariantAttributeValueRequest(

        @NotBlank
        @Size(max = 50)
        String value,

        @NotBlank
        @Size(max = 30)
        @Pattern(
                regexp = "^[A-Za-z0-9_-]+$",
                message = "Code may contain only letters, numbers, underscores and hyphens"
        )
        String code,

        @NotNull
        @Min(0)
        Integer sortOrder

) {}