package com.stockpilot.backend.org.controller;

import com.stockpilot.backend.org.dto.request.CreateBranchRequest;
import com.stockpilot.backend.org.dto.request.UpdateBranchRequest;
import com.stockpilot.backend.org.dto.request.UpdateBranchStatusRequest;
import com.stockpilot.backend.org.dto.response.BranchDto;
import com.stockpilot.backend.org.dto.response.DefaultBranchResponse;
import com.stockpilot.backend.org.enums.BranchStatus;
import com.stockpilot.backend.org.service.BranchService;
import com.stockpilot.backend.shared.api.ApiResponse;
import com.stockpilot.backend.shared.api.ApiRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.BRANCHES)
@RequiredArgsConstructor
@Validated
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<Page<BranchDto>>> getBranches(
            @RequestParam(required = false) BranchStatus status,
            Pageable pageable
    ) {

        Page<BranchDto> branches = branchService.getBranches(status, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(branches)
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<BranchDto>> createBranch(
            @Valid @RequestBody CreateBranchRequest request
    ) {

        BranchDto branch = branchService.createBranch(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(branch));
    }

    @GetMapping("/{branchId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<BranchDto>> getBranch(
            @PathVariable UUID branchId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        branchService.getBranch(branchId)
                )
        );
    }

    @PutMapping("/{branchId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<BranchDto>> updateBranch(
            @PathVariable UUID branchId,
            @Valid @RequestBody UpdateBranchRequest request
    ) {

        BranchDto branch = branchService.updateBranch(branchId, request);

        return ResponseEntity.ok(
                ApiResponse.success(branch)
        );
    }

    @PatchMapping("/{branchId}/status")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<BranchDto>> updateBranchStatus(
            @PathVariable UUID branchId,
            @Valid @RequestBody UpdateBranchStatusRequest request
    ) {

        BranchDto branch = branchService.updateBranchStatus(branchId, request);

        return ResponseEntity.ok(
                ApiResponse.success(branch)
        );
    }

    @PatchMapping("/{branchId}/default")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<DefaultBranchResponse>> setDefaultBranch(
            @PathVariable UUID branchId
    ) {

        DefaultBranchResponse response =
                branchService.setDefaultBranch(branchId);

        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }
}