package com.stockpilot.backend.org.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.stockpilot.backend.org.enums.BranchStatus;
import com.stockpilot.backend.org.enums.BranchType;

import java.time.Instant;
import java.util.UUID;

public record BranchDto(

        UUID id,

        String name,

        String code,

        BranchType branchType,

        String phone,

        String email,

        String addressLine1,

        String city,

        @JsonProperty("isDefault")
        boolean defaultBranch,

        BranchStatus status,

        BranchManagerDto manager,

        Instant createdAt,

        Instant updatedAt

) {
}