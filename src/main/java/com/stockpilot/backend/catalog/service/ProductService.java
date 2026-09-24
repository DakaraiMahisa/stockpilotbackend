package com.stockpilot.backend.catalog.service;

import com.stockpilot.backend.catalog.dto.request.CreateProductRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateProductRequest;
import com.stockpilot.backend.catalog.dto.response.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

    ProductDto createProduct(CreateProductRequest request);

    ProductDto updateProduct(
            UUID productId,
            UpdateProductRequest request
    );

    ProductDto getProductById(UUID productId);

    Page<ProductDto> getProducts(
            String search,
            UUID categoryId,
            UUID brandId,
            Boolean active,
            Pageable pageable
    );

    ProductDto getProductBySku(String sku);

    void deactivateProduct(UUID productId);

    void activateProduct(UUID productId);
}