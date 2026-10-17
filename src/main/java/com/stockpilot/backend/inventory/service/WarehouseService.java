package com.stockpilot.backend.inventory.service;

import com.stockpilot.backend.inventory.dto.request.WarehouseCreateRequest;
import com.stockpilot.backend.inventory.dto.request.WarehouseUpdateRequest;
import com.stockpilot.backend.inventory.dto.response.WarehouseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WarehouseService {

    Page<WarehouseResponse> getWarehouses(
            UUID branchId,
            Pageable pageable
    );

    WarehouseResponse getWarehouseById(UUID warehouseId);

    WarehouseResponse createWarehouse(WarehouseCreateRequest request);

    WarehouseResponse updateWarehouse(
            UUID warehouseId,
            WarehouseUpdateRequest request
    );

    void deactivateWarehouse(UUID warehouseId);

    void activateWarehouse(UUID warehouseId);
}
