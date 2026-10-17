package com.stockpilot.backend.inventory.mapper;

import com.stockpilot.backend.inventory.dto.response.BinLocationResponse;
import com.stockpilot.backend.inventory.entity.BinLocation;
import org.springframework.stereotype.Component;

@Component
public class BinLocationMapper {

    public BinLocationResponse toResponse(BinLocation binLocation) {
        return new BinLocationResponse(
                binLocation.getId(),
                binLocation.getWarehouseId(),
                binLocation.getParentBinId(),
                binLocation.getCode(),
                binLocation.getName(),
                binLocation.getBinType(),
                binLocation.isActive(),
                binLocation.getCreatedAt(),
                binLocation.getUpdatedAt(),
                binLocation.getVersion()
        );
    }
}
