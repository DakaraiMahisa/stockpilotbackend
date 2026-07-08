package com.stockpilot.backend.org.mapper;


import com.stockpilot.backend.org.dto.request.BusinessConfigUpdateRequest;
import com.stockpilot.backend.org.dto.response.BusinessConfigDto;
import com.stockpilot.backend.org.entity.BusinessConfig;
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
public interface BusinessConfigMapper {

    BusinessConfigDto toDto(BusinessConfig businessConfig);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromRequest(
            BusinessConfigUpdateRequest request,
            @MappingTarget BusinessConfig businessConfig
    );
}