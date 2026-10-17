package com.stockpilot.backend.inventory.service.impl;

import com.stockpilot.backend.inventory.dto.request.WarehouseCreateRequest;
import com.stockpilot.backend.inventory.dto.request.WarehouseUpdateRequest;
import com.stockpilot.backend.inventory.dto.response.WarehouseResponse;
import com.stockpilot.backend.inventory.entity.Warehouse;
import com.stockpilot.backend.inventory.enums.WarehouseStatus;
import com.stockpilot.backend.inventory.mapper.WarehouseMapper;
import com.stockpilot.backend.inventory.repository.WarehouseRepository;
import com.stockpilot.backend.inventory.service.WarehouseService;
import com.stockpilot.backend.org.entity.Branch;
import com.stockpilot.backend.org.repository.BranchRepository;
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
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final BranchRepository branchRepository;
    private final WarehouseMapper warehouseMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    @Transactional
    public WarehouseResponse createWarehouse(WarehouseCreateRequest request) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Branch branch = branchRepository
                .findByIdAndTenantIdAndDeletedFalse(
                        request.branchId(),
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Branch not found.")
                );

        Warehouse existingWarehouse = warehouseRepository
                .findByTenantIdAndBranchIdAndCodeAndDeletedFalse(
                        tenantId,
                        branch.getId(),
                        request.code().trim()
                )
                .orElse(null);

        if (existingWarehouse != null) {
            throw new DuplicateResourceException(
                    "Warehouse code already exists in this branch."
            );
        }

        Warehouse warehouse = Warehouse.builder()
                .tenantId(tenantId)
                .branchId(branch.getId())
                .name(request.name().trim())
                .code(request.code().trim())
                .addressLine(request.addressLine())
                .defaultWarehouse(false)
                .binTrackingEnabled(request.binTrackingEnabled())
                .status(WarehouseStatus.ACTIVE)
                .build();

        if (request.defaultWarehouse()) {
            warehouseRepository
                    .findByTenantIdAndBranchIdAndDefaultWarehouseTrueAndDeletedFalse(
                            tenantId,
                            branch.getId()
                    )
                    .ifPresent(Warehouse::removeAsDefault);

            warehouse.setDefaultWarehouse(true);
        }

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        return warehouseMapper.toWarehouseResponse(savedWarehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouseById(UUID warehouseId) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Warehouse warehouse = warehouseRepository
                .findByIdAndTenantIdAndDeletedFalse(
                        warehouseId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Warehouse not found.")
                );

        return warehouseMapper.toWarehouseResponse(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseResponse> getWarehouses(
            UUID branchId,
            Pageable pageable
    ) {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Page<Warehouse> warehouses;

        if (branchId != null) {
            warehouses = warehouseRepository
                    .findByTenantIdAndBranchIdAndDeletedFalse(
                            tenantId,
                            branchId,
                            pageable
                    );
        } else {
            warehouses = warehouseRepository
                    .findByTenantIdAndDeletedFalse(
                            tenantId,
                            pageable
                    );
        }

        return warehouses.map(warehouseMapper::toWarehouseResponse);
    }

    @Override
    @Transactional
    public WarehouseResponse updateWarehouse(
            UUID warehouseId,
            WarehouseUpdateRequest request
    ){
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Warehouse warehouse = warehouseRepository
                .findByIdAndTenantIdAndDeletedFalse(warehouseId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Warehouse not found.")
                );

        warehouse.setName(request.name().trim());
        warehouse.setAddressLine(request.addressLine());

        if (request.defaultWarehouse() != null) {
            if (request.defaultWarehouse()) {
                warehouseRepository
                        .findByTenantIdAndBranchIdAndDefaultWarehouseTrueAndDeletedFalse(
                                tenantId,
                                warehouse.getBranchId()
                        )
                        .filter(existingDefault ->
                                !existingDefault.getId().equals(warehouse.getId())
                        )
                        .ifPresent(existingDefault -> {
                            existingDefault.removeAsDefault();
                            warehouseRepository.saveAndFlush(existingDefault);
                        });

                warehouse.setDefaultWarehouse(true);
            } else {
                warehouse.setDefaultWarehouse(false);
            }
        }

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);

        return warehouseMapper.toWarehouseResponse(updatedWarehouse);
    }

    @Override
    @Transactional
    public void deactivateWarehouse(UUID warehouseId) {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Warehouse warehouse = warehouseRepository
                .findByIdAndTenantIdAndDeletedFalse(warehouseId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Warehouse not found.")
                );

        // TODO: Verify that the warehouse has zero active stock
        // before allowing deactivation.

        warehouse.deactivate();

        warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public void activateWarehouse(UUID warehouseId) {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Warehouse warehouse = warehouseRepository
                .findByIdAndTenantIdAndDeletedFalse(warehouseId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found."));

        warehouse.activate();
        warehouseRepository.save(warehouse);
    }
}
