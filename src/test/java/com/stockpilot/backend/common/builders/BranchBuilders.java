package com.stockpilot.backend.common.builders;

import com.stockpilot.backend.org.dto.request.CreateBranchRequest;
import com.stockpilot.backend.org.dto.request.UpdateBranchRequest;
import com.stockpilot.backend.org.dto.response.BranchDto;
import com.stockpilot.backend.org.entity.Branch;
import com.stockpilot.backend.org.enums.BranchStatus;
import com.stockpilot.backend.org.enums.BranchType;

import java.time.Instant;
import java.util.UUID;

public final class BranchBuilders {

    private BranchBuilders() {
    }

    public static Branch.BranchBuilder aBranch() {
        return Branch.builder()
                .id(UUID.randomUUID())
                .tenantId(UUID.randomUUID())
                .name("Head Office")
                .code("HQ001")
                .branchType(BranchType.RETAIL)
                .phone("+263771234567")
                .email("branch@test.com")
                .addressLine1("123 Main Street")
                .city("Harare")
                .defaultBranch(false)
                .status(BranchStatus.DRAFT);
    }

    public static CreateBranchRequest createBranchRequest() {
        return new CreateBranchRequest(
                "Head Office",
                "HQ001",
                BranchType.RETAIL,
                "+263771234567",
                "branch@test.com",
                "123 Main Street",
                "Harare",
                null
        );
    }

    public static UpdateBranchRequest updateBranchRequest() {
        return new UpdateBranchRequest(
                "Head Office Updated",
                "+263771111111",
                "updated@test.com",
                "456 Second Street",
                "Bulawayo",
                null
        );
    }

    public static BranchDto branchDto() {
        return new BranchDto(
                UUID.randomUUID(),
                "Head Office",
                "HQ001",
                BranchType.RETAIL,
                "+263771234567",
                "branch@test.com",
                "123 Main Street",
                "Harare",
                false,
                BranchStatus.DRAFT,
                null,
                Instant.now(),
                Instant.now()
        );
    }
}