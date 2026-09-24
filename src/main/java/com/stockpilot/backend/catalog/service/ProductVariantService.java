package com.stockpilot.backend.catalog.service;


import com.stockpilot.backend.catalog.dto.request.CreateProductVariantRequest;
import com.stockpilot.backend.catalog.dto.response.ProductVariantDto;
import com.stockpilot.backend.catalog.dto.request.UpdateProductVariantRequest;
import com.stockpilot.backend.catalog.dto.response.VariantAttributeDto;

import java.util.List;
import java.util.UUID;

public interface ProductVariantService {

    List<ProductVariantDto> createVariants(
            UUID productId,
            List<CreateProductVariantRequest> requests
    );

    List<ProductVariantDto> getVariantsByProductId(
            UUID productId
    );

    List<VariantAttributeDto> getVariantAttributes(
            UUID productId
    );
    ProductVariantDto getVariantById(
            UUID productId,
            UUID variantId
    );

    ProductVariantDto updateVariant(
            UUID productId,
            UUID variantId,
            UpdateProductVariantRequest request
    );

    void deactivateVariant(
            UUID productId,
            UUID variantId
    );
    void deleteVariant(
            UUID productId,
            UUID variantId
    );

    void assignVariantAttribute(
            UUID productId,
            UUID attributeId
    );

    void removeVariantAttribute(
            UUID productId,
            UUID attributeId
    );

}