package com.stockpilot.backend.catalog.mapper;

import com.stockpilot.backend.catalog.dto.response.ProductVariantDto;
import com.stockpilot.backend.catalog.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    @Mapping(target = "productId", source = "product.id")
    ProductVariantDto toDto(ProductVariant variant);
}