package com.stockpilot.backend.catalog.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record CategoryTreeDto(

        UUID id,

        String name,

        String code,

        UUID parentId,

        String description,

        Integer level,

        boolean leaf,

        Integer sortOrder,

        boolean active,

        List<CategoryTreeDto> children

) {
}