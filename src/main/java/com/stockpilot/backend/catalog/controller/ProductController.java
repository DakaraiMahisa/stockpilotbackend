package com.stockpilot.backend.catalog.controller;

import com.stockpilot.backend.catalog.dto.request.CreateProductRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateProductRequest;
import com.stockpilot.backend.catalog.dto.response.ProductDto;

import com.stockpilot.backend.catalog.service.ProductService;
import com.stockpilot.backend.shared.api.ApiResponse;
import com.stockpilot.backend.shared.api.ApiMessages;
import com.stockpilot.backend.shared.api.ApiRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.PRODUCTS)
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductPermissions).CREATE)"
    )
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
            @Valid @RequestBody CreateProductRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                productService.createProduct(request),
                                ApiMessages.PRODUCT_CREATED
                        )
                );
    }

    @PutMapping("/{productId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductPermissions).UPDATE)"
    )
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productService.updateProduct(
                                productId,
                                request
                        ),
                        ApiMessages.PRODUCT_UPDATED
                )
        );
    }

    @GetMapping
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductPermissions).READ)"
    )
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID brandId,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(
                    size = 20,
                    sort = "name",
                    direction = Sort.Direction.ASC
            ) Pageable pageable
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productService.getProducts(
                                search,
                                categoryId,
                                brandId,
                                active,
                                pageable
                        ),
                        ApiMessages.PRODUCTS_RETRIEVED
                )
        );
    }

    @GetMapping("/{productId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductPermissions).READ)"
    )
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(
            @PathVariable UUID productId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productService.getProductById(productId),
                        ApiMessages.PRODUCT_RETRIEVED
                )
        );
    }

    @GetMapping("/sku/{sku}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductPermissions).READ)"
    )
    public ResponseEntity<ApiResponse<ProductDto>> getProductBySku(
            @PathVariable String sku
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        productService.getProductBySku(sku),
                        ApiMessages.PRODUCT_RETRIEVED
                )
        );
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.catalog.permissions.ProductPermissions).DELETE)"
    )
    public ResponseEntity<ApiResponse<Void>> deactivateProduct(
            @PathVariable UUID productId
    ) {

        productService.deactivateProduct(productId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        ApiMessages.PRODUCT_DEACTIVATED
                )
        );
    }
}