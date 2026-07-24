package com.stockpilot.backend.catalog.mapper;

import com.stockpilot.backend.catalog.dto.request.CreateCategoryRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateCategoryRequest;
import com.stockpilot.backend.catalog.dto.response.CategoryDto;
import com.stockpilot.backend.catalog.dto.response.CategoryTreeDto;
import com.stockpilot.backend.catalog.entity.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "parent", ignore = true)
    Category toEntity(CreateCategoryRequest request);

    @Mapping(target = "parentId", source = "parent.id")
    CategoryDto toDto(Category category);

    @Mapping(target = "level", ignore = true)
    @Mapping(target = "leaf", ignore = true)
    @Mapping(target = "children", ignore = true)
    CategoryTreeDto toTreeDto(Category category);

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "code", ignore = true)
    void updateEntityFromRequest(
            UpdateCategoryRequest request,
            @MappingTarget Category category
    );
}