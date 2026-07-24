package com.stockpilot.backend.catalog.provider;


import com.stockpilot.backend.catalog.entity.Category;
import com.stockpilot.backend.catalog.repository.CategoryRepository;

import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryProvider {

    private final CategoryRepository categoryRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public Category getCategory(UUID categoryId) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        return categoryRepository
                .findByIdAndTenantIdAndDeletedFalse(categoryId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found")
                );
    }
}