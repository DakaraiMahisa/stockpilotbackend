package com.stockpilot.backend.catalog.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateCategoryRequest(

        @NotBlank(message = "Category name is required")
        String name,

        @NotBlank(message = "Category code is required")
        String code,

        String description,

        UUID parentId,

        @PositiveOrZero(message = "Sort order cannot be negative")
        Integer sortOrder
) {
}
