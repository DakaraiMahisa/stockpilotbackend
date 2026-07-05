package com.stockpilot.backend.org.service.impl;

import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.identity.domain.repository.UserRepository;
import com.stockpilot.backend.org.dto.request.CreateBranchRequest;
import com.stockpilot.backend.org.dto.request.UpdateBranchRequest;
import com.stockpilot.backend.org.dto.request.UpdateBranchStatusRequest;
import com.stockpilot.backend.org.dto.response.BranchDto;
import com.stockpilot.backend.org.dto.response.DefaultBranchResponse;
import com.stockpilot.backend.org.entity.Branch;
import com.stockpilot.backend.org.enums.BranchStatus;
import com.stockpilot.backend.org.event.BranchStatusChangedEvent;
import com.stockpilot.backend.org.mapper.BranchMapper;
import com.stockpilot.backend.org.repository.BranchRepository;
import com.stockpilot.backend.org.service.BranchService;
import com.stockpilot.backend.org.specifications.BranchSpecifications;
import com.stockpilot.backend.shared.exception.*;
import com.stockpilot.backend.shared.utils.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final BranchMapper branchMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public BranchDto createBranch(CreateBranchRequest request) {

        UUID tenantId = getCurrentTenantId();

        // TODO: Enforce subscription branch limit once Subscription module is implemented.

        validateUniqueBranchName(request.name(), tenantId);
        validateUniqueBranchCode(request.code(), tenantId);

        Branch branch = branchMapper.toEntity(request);
        branch.setTenantId(tenantId);

        if (request.managerId() != null) {
            User manager = getManagerOrThrow(request.managerId(), tenantId);
            branch.setManager(manager);
        }

        Branch savedBranch = branchRepository.save(branch);

        log.info(
                "Created branch '{}' (code={}) for tenant {}",
                savedBranch.getName(),
                savedBranch.getCode(),
                tenantId
        );

        return branchMapper.toDto(savedBranch);
    }

    private void validateUniqueBranchName(
            String name,
            UUID tenantId
    ) {
        if (branchRepository.existsByNameAndTenantIdAndDeletedFalse(name, tenantId)) {
            throw new DuplicateResourceException(
                    "Branch name already exists."
            );
        }
    }

    private void validateUniqueBranchCode(
            String code,
            UUID tenantId
    ) {
        if (branchRepository.existsByCodeAndTenantIdAndDeletedFalse(code, tenantId)) {
            throw new DuplicateResourceException(
                    "Branch code already exists."
            );
        }
    }

    @Override
    public BranchDto updateBranch(
            UUID branchId,
            UpdateBranchRequest request
    ) {

        UUID tenantId = getCurrentTenantId();

        Branch branch = getBranchOrThrow(branchId, tenantId);

        if (request.name() != null
                && !request.name().equals(branch.getName())) {

            validateUniqueBranchName(request.name(), tenantId);
        }

        branchMapper.updateEntityFromRequest(request, branch);

        if (request.managerId() != null) {
            User manager = getManagerOrThrow(request.managerId(), tenantId);
            branch.setManager(manager);
        }

        Branch updatedBranch = branchRepository.save(branch);

        log.info(
                "Updated branch '{}' for tenant {}",
                updatedBranch.getCode(),
                tenantId
        );

        return branchMapper.toDto(updatedBranch);
    }


    @Override
    public BranchDto updateBranchStatus(
            UUID branchId,
            UpdateBranchStatusRequest request
    ) {

        Branch branch = getBranchOrThrow(
                branchId,
                getCurrentTenantId()
        );

        BranchStatus currentStatus = branch.getStatus();
        BranchStatus newStatus = request.status();

        validateStatusTransition(branch, newStatus);

        if (currentStatus == newStatus) {
            return branchMapper.toDto(branch);
        }

        switch (newStatus) {
            case ACTIVE -> branch.activate();

            case INACTIVE -> {
                ensureNotLastActiveBranch(branch);
                branch.deactivate();
            }

            case ARCHIVED -> {
                ensureNotLastActiveBranch(branch);
                branch.archive();
            }

            default -> throw new InvalidOperationException(
                    "Unsupported branch status transition."
            );
        }

        Branch updatedBranch = branchRepository.save(branch);

        eventPublisher.publishEvent(
                new BranchStatusChangedEvent(
                        updatedBranch.getId(),
                        updatedBranch.getTenantId(),
                        currentStatus,
                        newStatus,
                        Instant.now()
                )
        );

        log.info(
                "Changed branch '{}' status from {} to {}",
                updatedBranch.getCode(),
                currentStatus,
                newStatus
        );

        return branchMapper.toDto(updatedBranch);
    }

    @Override
    public DefaultBranchResponse setDefaultBranch(UUID branchId) {

        UUID tenantId = getCurrentTenantId();

        Branch newDefaultBranch = getBranchOrThrow(branchId, tenantId);

        if (!newDefaultBranch.isActive()) {
            throw new InvalidOperationException(
                    "Only active branches can be set as the default branch."
            );
        }

        Branch currentDefaultBranch = branchRepository
                .findByTenantIdAndDefaultBranchTrueAndDeletedFalse(tenantId)
                .orElse(null);

        if (currentDefaultBranch != null
                && currentDefaultBranch.getId().equals(newDefaultBranch.getId())) {

            return new DefaultBranchResponse(
                    branchMapper.toDto(currentDefaultBranch),
                    branchMapper.toDto(newDefaultBranch)
            );
        }

        if (currentDefaultBranch != null) {
            currentDefaultBranch.removeAsDefault();
        }

        newDefaultBranch.markAsDefault();
        log.info(
                "Default branch changed to '{}' for tenant {}",
                newDefaultBranch.getCode(),
                tenantId
        );

        return new DefaultBranchResponse(
                currentDefaultBranch == null
                        ? null
                        : branchMapper.toDto(currentDefaultBranch),
                branchMapper.toDto(newDefaultBranch)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BranchDto> getBranches(
            BranchStatus status,
            Pageable pageable
    ) {

        Specification<Branch> specification = Specification
                .where(BranchSpecifications.belongsToTenant(getCurrentTenantId()))
                .and(BranchSpecifications.notDeleted())
                .and(BranchSpecifications.hasStatus(status));

        return branchRepository
                .findAll(specification, pageable)
                .map(branchMapper::toDto);
    }


    private User getManagerOrThrow(
            UUID managerId,
            UUID tenantId
    ) {

        User manager = userRepository
                .findByIdAndTenantId(managerId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Branch manager with ID " + managerId + " not found."
                        ));
        if (Boolean.FALSE.equals(manager.getActive())) {
            throw new BusinessRuleException(
                    "Inactive users cannot be assigned as branch managers."
            );
        }

        return manager;
    }

    private Branch getBranchOrThrow(
            UUID branchId,
            UUID tenantId
    ) {
        return branchRepository
                .findByIdAndTenantIdAndDeletedFalse(branchId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Branch with ID " + branchId + " not found."
                        ));
    }


    private void validateStatusTransition(
            Branch branch,
            BranchStatus newStatus
    ) {

        BranchStatus current = branch.getStatus();

        if (current == BranchStatus.ARCHIVED) {
            throw new InvalidOperationException(
                    "Archived branches cannot be modified."
            );
        }

        switch (current) {

            case DRAFT -> {
                if (newStatus != BranchStatus.ACTIVE
                        && newStatus != BranchStatus.ARCHIVED) {
                    throw new InvalidOperationException(
                            "Invalid branch status transition."
                    );
                }
            }

            case ACTIVE -> {
                if (newStatus != BranchStatus.INACTIVE) {
                    throw new InvalidOperationException(
                            "Invalid branch status transition."
                    );
                }
            }

            case INACTIVE -> {
                if (newStatus != BranchStatus.ACTIVE
                        && newStatus != BranchStatus.ARCHIVED) {
                    throw new InvalidOperationException(
                            "Invalid branch status transition."
                    );
                }
            }

        }
    }

    private void ensureNotLastActiveBranch(
            Branch branch
    ) {

        if (!branch.isActive()) {
            return;
        }

        long activeBranches = branchRepository.countByTenantIdAndStatusAndDeletedFalse(
                getCurrentTenantId(),
                BranchStatus.ACTIVE
        );

        if (activeBranches <= 1) {
            throw new InactiveBranchException(
                    "At least one active branch must exist."
            );
        }
    }

    private UUID getCurrentTenantId() {
        return TenantContext.getTenantId();
    }
}
