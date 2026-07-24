package com.stockpilot.backend.catalog.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MoveCategoryRequest(

        UUID newParentId

) {
}