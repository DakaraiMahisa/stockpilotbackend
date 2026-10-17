package com.stockpilot.backend.inventory.service;

import com.stockpilot.backend.inventory.dto.request.BinLocationCreateRequest;
import com.stockpilot.backend.inventory.dto.request.BinLocationUpdateRequest;
import com.stockpilot.backend.inventory.dto.response.BinLocationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BinLocationService {

    Page<BinLocationResponse> getBins(
            UUID warehouseId,
            UUID parentBinId,
            Pageable pageable
    );

    BinLocationResponse createBin(
            UUID warehouseId,
            BinLocationCreateRequest request
    );

    BinLocationResponse updateBin(
            UUID warehouseId,
            UUID binId,
            BinLocationUpdateRequest request
    );
}
