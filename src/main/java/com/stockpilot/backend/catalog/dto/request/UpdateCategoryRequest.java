package com.stockpilot.backend.catalog.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record UpdateCategoryRequest(

        String name,

        String description,

        @PositiveOrZero(message = "Sort order cannot be negative")
        Integer sortOrder,

        Boolean active
) {
}
