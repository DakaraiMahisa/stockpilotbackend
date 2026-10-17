package com.stockpilot.backend.inventory.service.impl;


import com.stockpilot.backend.inventory.dto.request.BinLocationCreateRequest;
import com.stockpilot.backend.inventory.dto.response.BinLocationResponse;
import com.stockpilot.backend.inventory.dto.request.BinLocationUpdateRequest;
import com.stockpilot.backend.inventory.entity.BinLocation;
import com.stockpilot.backend.inventory.enums.BinType;
import com.stockpilot.backend.inventory.entity.Warehouse;
import com.stockpilot.backend.inventory.enums.WarehouseStatus;
import com.stockpilot.backend.inventory.mapper.BinLocationMapper;
import com.stockpilot.backend.inventory.repository.BinLocationRepository;
import com.stockpilot.backend.inventory.repository.WarehouseRepository;
import com.stockpilot.backend.inventory.service.BinLocationService;
import com.stockpilot.backend.shared.exception.base.BusinessException;
import com.stockpilot.backend.shared.exception.base.DuplicateResourceException;
import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BinLocationServiceImpl implements BinLocationService {

    private final BinLocationRepository binLocationRepository;
    private final WarehouseRepository warehouseRepository;
    private final BinLocationMapper binLocationMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    @Transactional(readOnly = true)
    public Page<BinLocationResponse> getBins(
            UUID warehouseId,
            UUID parentBinId,
            Pageable pageable
    ) {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        getWarehouse(warehouseId, tenantId);

        Page<BinLocation> bins;

        if (parentBinId == null) {
            bins = binLocationRepository
                    .findByTenantIdAndWarehouseIdAndDeletedFalse(
                            tenantId, warehouseId, pageable
                    );
        } else {
            getBin(warehouseId, parentBinId, tenantId);

            bins = binLocationRepository
                    .findByTenantIdAndWarehouseIdAndParentBinIdAndDeletedFalse(
                            tenantId, warehouseId, parentBinId, pageable
                    );
        }

        return bins.map(binLocationMapper::toResponse);
    }

    @Override
    @Transactional
    public BinLocationResponse createBin(
            UUID warehouseId,
            BinLocationCreateRequest request
    ) {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Warehouse warehouse = getWarehouse(warehouseId, tenantId);
        validateWarehouseForChanges(warehouse);

        String code = request.code().trim();

        if (binLocationRepository
                .existsByTenantIdAndWarehouseIdAndCodeAndDeletedFalse(
                        tenantId, warehouseId, code
                )) {
            throw new DuplicateResourceException(
                    "A bin location with this code already exists in the warehouse."
            );
        }

        validateParent(
                tenantId,
                warehouseId,
                request.parentBinId(),
                request.binType()
        );

        BinLocation bin = new BinLocation();
        bin.setTenantId(tenantId);
        bin.setWarehouseId(warehouseId);
        bin.setParentBinId(request.parentBinId());
        bin.setCode(code);
        bin.setName(request.name() == null ? null : request.name().trim());
        bin.setBinType(request.binType());
        bin.setActive(true);

        return binLocationMapper.toResponse(
                binLocationRepository.save(bin)
        );
    }

    @Override
    @Transactional
    public BinLocationResponse updateBin(
            UUID warehouseId,
            UUID binId,
            BinLocationUpdateRequest request
    ) {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Warehouse warehouse = getWarehouse(warehouseId, tenantId);
        validateWarehouseForChanges(warehouse);

        BinLocation bin = getBin(warehouseId, binId, tenantId);

        String code = request.code().trim();

        binLocationRepository
                .findByTenantIdAndWarehouseIdAndCodeAndDeletedFalse(
                        tenantId, warehouseId, code
                )
                .filter(existing -> !existing.getId().equals(binId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "A bin location with this code already exists in the warehouse."
                    );
                });

        BinType updatedType = request.binType() == null
                ? bin.getBinType()
                : request.binType();

        validateParent(
                tenantId,
                warehouseId,
                bin.getParentBinId(),
                updatedType
        );

        if (updatedType != bin.getBinType()
                && binLocationRepository
                .countByTenantIdAndWarehouseIdAndParentBinIdAndDeletedFalse(
                        tenantId, warehouseId, binId
                ) > 0) {
            throw new BusinessException(
                    "Cannot change the type of a bin location that has child locations."
            );
        }

        bin.setCode(code);

        if (request.name() != null) {
            bin.setName(request.name().trim());
        }

        bin.setBinType(updatedType);

        if (request.active() != null) {
            bin.setActive(request.active());
        }

        return binLocationMapper.toResponse(
                binLocationRepository.save(bin)
        );
    }

    private Warehouse getWarehouse(UUID warehouseId, UUID tenantId) {
        return warehouseRepository
                .findByIdAndTenantIdAndDeletedFalse(warehouseId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Warehouse not found.")
                );
    }

    private BinLocation getBin(
            UUID warehouseId,
            UUID binId,
            UUID tenantId
    ) {
        return binLocationRepository
                .findByIdAndTenantIdAndWarehouseIdAndDeletedFalse(
                        binId, tenantId, warehouseId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Bin location not found.")
                );
    }

    private void validateWarehouseForChanges(Warehouse warehouse) {
        if (warehouse.getStatus() != WarehouseStatus.ACTIVE) {
            throw new BusinessException(
                    "Cannot modify bin locations in an inactive warehouse."
            );
        }

        if (!warehouse.isBinTrackingEnabled()) {
            throw new BusinessException(
                    "Bin tracking is not enabled for this warehouse."
            );
        }
    }

    private void validateParent(
            UUID tenantId,
            UUID warehouseId,
            UUID parentBinId,
            BinType binType
    ) {
        if (binType == BinType.ZONE) {
            if (parentBinId != null) {
                throw new BusinessException(
                        "A zone cannot have a parent bin location."
                );
            }
            return;
        }

        if (parentBinId == null) {
            throw new BusinessException(
                    "A parent bin location is required for this bin type."
            );
        }

        BinLocation parent = getBin(warehouseId, parentBinId, tenantId);

        if (!parent.isActive()) {
            throw new BusinessException(
                    "The parent bin location must be active."
            );
        }

        BinType requiredParentType = switch (binType) {
            case AISLE -> BinType.ZONE;
            case RACK -> BinType.AISLE;
            case SHELF -> BinType.RACK;
            case BIN -> BinType.SHELF;
            case ZONE -> throw new IllegalStateException(
                    "Zone parent validation should already be handled."
            );
        };

        if (parent.getBinType() != requiredParentType) {
            throw new BusinessException(
                    "Invalid parent type for " + binType
                            + ". Expected parent type: " + requiredParentType + "."
            );
        }
    }
}
