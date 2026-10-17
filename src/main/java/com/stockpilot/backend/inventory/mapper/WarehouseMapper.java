package com.stockpilot.backend.inventory.mapper;

import com.stockpilot.backend.inventory.dto.response.WarehouseResponse;
import com.stockpilot.backend.inventory.entity.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public WarehouseResponse toWarehouseResponse(Warehouse warehouse) {
        return new WarehouseResponse(
                warehouse.getId(),
                warehouse.getBranchId(),
                warehouse.getName(),
                warehouse.getCode(),
                warehouse.getAddressLine(),
                warehouse.isDefaultWarehouse(),
                warehouse.isBinTrackingEnabled(),
                warehouse.getStatus(),
                warehouse.getCreatedAt(),
                warehouse.getUpdatedAt(),
                warehouse.getVersion()
        );
    }
}
