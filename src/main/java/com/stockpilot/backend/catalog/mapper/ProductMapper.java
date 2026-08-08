package com.stockpilot.backend.catalog.mapper;

import com.stockpilot.backend.catalog.dto.request.CreateProductRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateProductRequest;
import com.stockpilot.backend.catalog.dto.response.ProductDto;
import com.stockpilot.backend.catalog.entity.Product;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)

    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "taxClass", ignore = true)

    @Mapping(target = "hasVariants", ignore = true)
    @Mapping(target = "active", ignore = true)

    Product toEntity(CreateProductRequest request);

    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "brandName", source = "brand.name")

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")

    @Mapping(target = "retailPrice", ignore = true)
    @Mapping(target = "stockQty", ignore = true)
    ProductDto toDto(Product product);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)

    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "barcode", ignore = true)

    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "taxClass", ignore = true)

    @Mapping(target = "hasVariants", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "unitOfMeasure", ignore = true)

    void updateEntityFromRequest(
            UpdateProductRequest request,
            @MappingTarget Product product
    );
}