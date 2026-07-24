package com.stockpilot.backend.catalog.controller;

import com.stockpilot.backend.catalog.dto.request.CreateCategoryRequest;
import com.stockpilot.backend.catalog.dto.request.MoveCategoryRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateCategoryRequest;
import com.stockpilot.backend.catalog.dto.response.CategoryDto;
import com.stockpilot.backend.catalog.dto.response.CategoryTreeDto;
import com.stockpilot.backend.catalog.service.CategoryService;
import com.stockpilot.backend.shared.api.ApiMessages;
import com.stockpilot.backend.shared.api.ApiResponse;
import com.stockpilot.backend.shared.api.ApiRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.CATEGORIES)
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.CategoryPermissions).CREATE)")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                categoryService.createCategory(request),
                                ApiMessages.CATEGORY_CREATED
                        )
                );
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.CategoryPermissions).UPDATE)")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        categoryService.updateCategory(categoryId, request),
                        ApiMessages.CATEGORY_UPDATED
                )
        );
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.CategoryPermissions).DELETE)")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable UUID categoryId
    ) {

        categoryService.deleteCategory(categoryId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        ApiMessages.CATEGORY_DELETED
                )
        );
    }

    @GetMapping("/{categoryId}")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.CategoryPermissions).READ)")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategory(
            @PathVariable UUID categoryId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        categoryService.getCategoryById(categoryId),
                        ApiMessages.CATEGORY_RETRIEVED
                )
        );
    }

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.CategoryPermissions).READ)")
    public ResponseEntity<ApiResponse<List<CategoryTreeDto>>> getCategoryTree() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        categoryService.getCategoryTree(),
                        ApiMessages.CATEGORY_TREE_RETRIEVED
                )
        );
    }

    @PatchMapping("/{categoryId}/move")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.CategoryPermissions).MOVE)")
    public ResponseEntity<ApiResponse<CategoryDto>> moveCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody MoveCategoryRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        categoryService.moveCategory(categoryId, request),
                        ApiMessages.CATEGORY_MOVED
                )
        );
    }
}