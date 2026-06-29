package com.stockpilot.backend.shared.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Pagination(
    Integer page,
    Integer size,
    Long totalElements,
    Integer totalPages
) {
}

