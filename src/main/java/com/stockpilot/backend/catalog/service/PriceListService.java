package com.stockpilot.backend.catalog.service;

import com.stockpilot.backend.catalog.dto.request.CreatePriceListRequest;
import com.stockpilot.backend.catalog.dto.request.PriceListItemRequest;
import com.stockpilot.backend.catalog.dto.request.UpdatePriceListRequest;
import com.stockpilot.backend.catalog.dto.response.PriceListDto;
import com.stockpilot.backend.catalog.dto.response.PriceListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PriceListService {

    PriceListDto create(
            UUID tenantId,
            CreatePriceListRequest request
    );

    PriceListDto getById(
            UUID tenantId,
            UUID priceListId
    );

    Page<PriceListDto> getAll(
            UUID tenantId,
            Pageable pageable
    );

    PriceListDto update(
            UUID tenantId,
            UUID priceListId,
            UpdatePriceListRequest request
    );

    void delete(
            UUID tenantId,
            UUID priceListId
    );

    List<PriceListItemDto> getItems(
            UUID tenantId,
            UUID priceListId
    );

    List<PriceListItemDto> upsertItems(
            UUID tenantId,
            UUID priceListId,
            List<PriceListItemRequest> requests
    );

    void deleteItem(
            UUID tenantId,
            UUID priceListId,
            UUID itemId
    );
}