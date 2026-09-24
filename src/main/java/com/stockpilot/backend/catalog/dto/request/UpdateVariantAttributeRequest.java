package com.stockpilot.backend.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateVariantAttributeRequest(

        @NotBlank
        @Size(max = 50)
        String name,

        @NotBlank
        @Size(max = 30)
        @Pattern(
                regexp = "^[A-Za-z0-9_-]+$",
                message = "Code may contain only letters, numbers, underscores and hyphens"
        )
        String code,

        @Size(max = 255)
        String description

) {}