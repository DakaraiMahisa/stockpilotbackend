package com.stockpilot.backend.org.repository;

import com.stockpilot.backend.org.entity.Branch;
import com.stockpilot.backend.org.enums.BranchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchRepository extends
        JpaRepository<Branch, UUID>,
        JpaSpecificationExecutor<Branch> {

    Optional<Branch> findByIdAndTenantIdAndDeletedFalse(
            UUID id,
            UUID tenantId
    );

    Optional<Branch> findByCodeAndTenantIdAndDeletedFalse(
            String code,
            UUID tenantId
    );

    Optional<Branch> findByNameAndTenantIdAndDeletedFalse(
            String name,
            UUID tenantId
    );

    boolean existsByCodeAndTenantIdAndDeletedFalse(
            String code,
            UUID tenantId
    );

    boolean existsByNameAndTenantIdAndDeletedFalse(
            String name,
            UUID tenantId
    );

    boolean existsByCodeAndTenantIdAndIdNotAndDeletedFalse(
            String code,
            UUID tenantId,
            UUID id
    );

    boolean existsByNameAndTenantIdAndIdNotAndDeletedFalse(
            String name,
            UUID tenantId,
            UUID id
    );

    Optional<Branch> findByTenantIdAndDefaultBranchTrueAndDeletedFalse(
            UUID tenantId
    );

    long countByTenantIdAndStatusAndDeletedFalse(
            UUID tenantId,
            BranchStatus status
    );
}
