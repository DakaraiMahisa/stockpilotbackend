package com.stockpilot.backend.org.service;


import com.stockpilot.backend.org.dto.request.CreateBranchRequest;
import com.stockpilot.backend.org.dto.request.UpdateBranchRequest;
import com.stockpilot.backend.org.dto.request.UpdateBranchStatusRequest;
import com.stockpilot.backend.org.dto.response.BranchDto;
import com.stockpilot.backend.org.dto.response.DefaultBranchResponse;
import com.stockpilot.backend.org.enums.BranchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BranchService {

    BranchDto createBranch(CreateBranchRequest request);

    BranchDto updateBranch(
            UUID branchId,
            UpdateBranchRequest request
    );

    BranchDto updateBranchStatus(
            UUID branchId,
            UpdateBranchStatusRequest request
    );

    DefaultBranchResponse setDefaultBranch(UUID branchId);

    Page<BranchDto> getBranches(
            BranchStatus status,
            Pageable pageable
    );
}