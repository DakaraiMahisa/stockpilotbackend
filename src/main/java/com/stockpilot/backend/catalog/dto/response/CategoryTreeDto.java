package com.stockpilot.backend.catalog.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record CategoryTreeDto(

        UUID id,

        String name,

        String code,

        Integer level,

        boolean leaf,

        Integer sortOrder,

        List<CategoryTreeDto> children

) {
}