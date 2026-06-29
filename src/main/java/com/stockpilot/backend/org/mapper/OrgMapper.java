package com.stockpilot.backend.org.mapper;


import com.stockpilot.backend.org.dto.OrganizationDto;
import com.stockpilot.backend.org.dto.OrganizationUpdateRequest;
import com.stockpilot.backend.org.entity.Organization;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OrgMapper {


    OrganizationDto toDto(Organization organization);


    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "logoUrl", ignore = true)
    void updateEntityFromRequest(
            OrganizationUpdateRequest request,
            @MappingTarget Organization organization
    );
}
