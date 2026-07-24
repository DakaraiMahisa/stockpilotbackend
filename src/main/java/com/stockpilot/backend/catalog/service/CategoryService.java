package com.stockpilot.backend.catalog.service;


import com.stockpilot.backend.catalog.dto.request.CreateCategoryRequest;
import com.stockpilot.backend.catalog.dto.request.MoveCategoryRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateCategoryRequest;
import com.stockpilot.backend.catalog.dto.response.CategoryDto;
import com.stockpilot.backend.catalog.dto.response.CategoryTreeDto;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryDto createCategory(CreateCategoryRequest request);

    CategoryDto updateCategory(
            UUID categoryId,
            UpdateCategoryRequest request
    );

    void deleteCategory(UUID categoryId);

    CategoryDto getCategoryById(UUID categoryId);

    List<CategoryTreeDto> getCategoryTree();

    CategoryDto moveCategory(
            UUID categoryId,
            MoveCategoryRequest request
    );
}