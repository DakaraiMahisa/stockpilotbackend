package com.stockpilot.backend.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record BrandCreateRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 20)
        @Pattern(regexp = "^[A-Z0-9_-]+$")
        String code,

        @Size(max = 500)
        String logoObjectKey,

        @Size(max = 200)
        String website
) {
}