package com.stockpilot.backend.catalog.service;


import com.stockpilot.backend.catalog.dto.request.BrandCreateRequest;
import com.stockpilot.backend.catalog.dto.request.BrandUpdateRequest;
import com.stockpilot.backend.catalog.dto.response.BrandDto;
import com.stockpilot.backend.catalog.dto.response.BrandSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface BrandService {


    BrandDto createBrand(BrandCreateRequest request);

    BrandDto updateBrand(UUID brandId, BrandUpdateRequest request);


    BrandDto getBrandById(UUID brandId);


    Page<BrandDto> getBrands(
            String search,
            Boolean active,
            Pageable pageable
    );


    List<BrandSummaryDto> getActiveBrands();

    void deactivateBrand(UUID brandId);
}
