package com.stockpilot.backend.org.mapper;

import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.org.dto.request.CreateBranchRequest;
import com.stockpilot.backend.org.dto.request.UpdateBranchRequest;
import com.stockpilot.backend.org.dto.response.BranchDto;
import com.stockpilot.backend.org.dto.response.BranchManagerDto;
import com.stockpilot.backend.org.entity.Branch;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface BranchMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "defaultBranch", ignore = true)
    @Mapping(target = "manager", ignore = true)
    Branch toEntity(CreateBranchRequest request);

    BranchDto toDto(Branch branch);

    BranchManagerDto toManagerDto(User user);

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "branchType", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "defaultBranch", ignore = true)
    @Mapping(target = "manager", ignore = true)
    void updateEntityFromRequest(
            UpdateBranchRequest request,
            @MappingTarget Branch branch
    );
}