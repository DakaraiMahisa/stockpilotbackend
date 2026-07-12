package com.stockpilot.backend.org.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateTaxClassRequest(

        @Size(max = 80)
        String name,

        @Size(max = 10)
        String hsnSacCode,

        @Size(max = 1000)
        String description

) {
}