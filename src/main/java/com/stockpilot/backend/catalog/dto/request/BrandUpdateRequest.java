package com.stockpilot.backend.catalog.dto.request;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record BrandUpdateRequest(

        @Size(max = 100)
        String name,

        @Pattern(regexp = "^[A-Z0-9_-]+$")
        @Size(max = 20)
        String code,

        @Size(max = 500)
        String logoObjectKey,

        @Size(max = 200)
        String website,

        Boolean active
) {
}