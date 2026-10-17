package com.stockpilot.backend.inventory.repository;


import com.stockpilot.backend.inventory.entity.BinLocation;
import com.stockpilot.backend.inventory.enums.BinType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BinLocationRepository
        extends JpaRepository<BinLocation, UUID> {

    Optional<BinLocation> findByIdAndTenantIdAndDeletedFalse(
            UUID id,
            UUID tenantId
    );

    Page<BinLocation> findByTenantIdAndWarehouseIdAndDeletedFalse(
            UUID tenantId,
            UUID warehouseId,
            Pageable pageable
    );

    Optional<BinLocation> findByIdAndTenantIdAndWarehouseIdAndDeletedFalse(
            UUID id,
            UUID tenantId,
            UUID warehouseId
    );

    Optional<BinLocation> findByTenantIdAndWarehouseIdAndCodeAndDeletedFalse(
            UUID tenantId,
            UUID warehouseId,
            String code
    );

    boolean existsByTenantIdAndWarehouseIdAndCodeAndDeletedFalse(
            UUID tenantId,
            UUID warehouseId,
            String code
    );

    Page<BinLocation> findByTenantIdAndWarehouseIdAndParentBinIdAndDeletedFalse(
            UUID tenantId,
            UUID warehouseId,
            UUID parentBinId,
            Pageable pageable
    );

    long countByTenantIdAndWarehouseIdAndDeletedFalse(
            UUID tenantId,
            UUID warehouseId
    );

    long countByTenantIdAndWarehouseIdAndParentBinIdAndDeletedFalse(
            UUID tenantId,
            UUID warehouseId,
            UUID parentBinId
    );

    long countByTenantIdAndWarehouseIdAndBinTypeAndDeletedFalse(
            UUID tenantId,
            UUID warehouseId,
            BinType binType
    );
}

