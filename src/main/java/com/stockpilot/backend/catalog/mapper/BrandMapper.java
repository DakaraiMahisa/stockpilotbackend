package com.stockpilot.backend.catalog.mapper;


import com.stockpilot.backend.catalog.dto.request.BrandCreateRequest;
import com.stockpilot.backend.catalog.dto.request.BrandUpdateRequest;
import com.stockpilot.backend.catalog.dto.response.BrandDto;
import com.stockpilot.backend.catalog.dto.response.BrandSummaryDto;
import com.stockpilot.backend.catalog.entity.Brand;
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
public interface BrandMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "active", ignore = true)
    Brand toEntity(BrandCreateRequest request);

    BrandDto toDto(Brand brand);

    BrandSummaryDto toSummaryDto(Brand brand);

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromRequest(
            BrandUpdateRequest request,
            @MappingTarget Brand brand
    );
}