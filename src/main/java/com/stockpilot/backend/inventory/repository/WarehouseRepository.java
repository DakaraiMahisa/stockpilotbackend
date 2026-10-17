package com.stockpilot.backend.inventory.repository;

import com.stockpilot.backend.inventory.entity.Warehouse;
import com.stockpilot.backend.inventory.enums.WarehouseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    Optional<Warehouse> findByIdAndTenantIdAndDeletedFalse(
            UUID id,
            UUID tenantId
    );

    Page<Warehouse> findByTenantIdAndDeletedFalse(
            UUID tenantId,
            Pageable pageable
    );

    Page<Warehouse> findByTenantIdAndBranchIdAndDeletedFalse(
            UUID tenantId,
            UUID branchId,
            Pageable pageable
    );

    Optional<Warehouse> findByTenantIdAndBranchIdAndCodeAndDeletedFalse(
            UUID tenantId,
            UUID branchId,
            String code
    );

    boolean existsByTenantIdAndBranchIdAndCodeAndDeletedFalse(
            UUID tenantId,
            UUID branchId,
            String code
    );

    Optional<Warehouse> findByTenantIdAndBranchIdAndDefaultWarehouseTrueAndDeletedFalse(
            UUID tenantId,
            UUID branchId
    );

    boolean existsByTenantIdAndBranchIdAndDefaultWarehouseTrueAndDeletedFalse(
            UUID tenantId,
            UUID branchId
    );

    long countByTenantIdAndBranchIdAndStatusAndDeletedFalse(
            UUID tenantId,
            UUID branchId,
            WarehouseStatus status
    );
}
