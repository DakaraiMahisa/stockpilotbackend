package com.stockpilot.backend.catalog.service.impl;
import com.stockpilot.backend.catalog.dto.request.CreateCategoryRequest;
import com.stockpilot.backend.catalog.dto.request.MoveCategoryRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateCategoryRequest;
import com.stockpilot.backend.catalog.dto.response.CategoryDto;
import com.stockpilot.backend.catalog.dto.response.CategoryTreeDto;
import com.stockpilot.backend.catalog.entity.Category;
import com.stockpilot.backend.catalog.event.CategoryCreatedEvent;
import com.stockpilot.backend.catalog.event.CategoryDeletedEvent;
import com.stockpilot.backend.catalog.event.CategoryMovedEvent;
import com.stockpilot.backend.catalog.event.CategoryUpdatedEvent;
import com.stockpilot.backend.catalog.mapper.CategoryMapper;
import com.stockpilot.backend.catalog.provider.CategoryProvider;
import com.stockpilot.backend.catalog.repository.CategoryRepository;
import com.stockpilot.backend.catalog.service.CategoryService;
import com.stockpilot.backend.catalog.tree.CategoryTreeBuilder;
import com.stockpilot.backend.shared.exception.base.BusinessRuleException;
import com.stockpilot.backend.shared.exception.base.DuplicateResourceException;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryProvider categoryProvider;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ApplicationEventPublisher eventPublisher;
    private final CategoryTreeBuilder categoryTreeBuilder;

    @Override
    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest request) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();
        UUID userId = authenticatedUserProvider.getCurrentUserId();

        validateUniqueCategoryCode(request.code(), tenantId);

        Category parent = resolveParent(request.parentId());

        validateUniqueSiblingName(
                request.name(),
                parent,
                tenantId
        );

        Category category = categoryMapper.toEntity(request);

        category.setTenantId(tenantId);
        category.setParent(parent);

        Category savedCategory = categoryRepository.save(category);

        eventPublisher.publishEvent(
                CategoryCreatedEvent.of(
                        savedCategory.getId(),
                        tenantId,
                        userId
                )
        );

        log.info(
                "Category created. Tenant={}, Category={}, CreatedBy={}",
                tenantId,
                savedCategory.getId(),
                userId
        );

        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(
            UUID categoryId,
            UpdateCategoryRequest request
    ) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();
        UUID userId = authenticatedUserProvider.getCurrentUserId();

        Category category = categoryProvider.getCategory(categoryId);

        validateUniqueSiblingNameForUpdate(
                category,
                request.name(),
                tenantId
        );

        categoryMapper.updateEntityFromRequest(request, category);

        Category updatedCategory = categoryRepository.save(category);

        eventPublisher.publishEvent(
                CategoryUpdatedEvent.of(
                        updatedCategory.getId(),
                        tenantId,
                        userId
                )
        );

        log.info(
                "Category updated. Tenant={}, Category={}, UpdatedBy={}",
                tenantId,
                updatedCategory.getId(),
                userId
        );

        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID categoryId) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();
        UUID userId = authenticatedUserProvider.getCurrentUserId();

        Category category = categoryProvider.getCategory(categoryId);

        validateCategoryDeletion(category);

        category.setDeleted(true);

        categoryRepository.save(category);

        eventPublisher.publishEvent(
                CategoryDeletedEvent.of(
                        category.getId(),
                        tenantId,
                        userId
                )
        );

        log.info(
                "Category deleted. Tenant={}, Category={}, DeletedBy={}",
                tenantId,
                category.getId(),
                userId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(UUID categoryId) {

        Category category = categoryProvider.getCategory(categoryId);

        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryTreeDto> getCategoryTree() {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        List<Category> categories =
                categoryRepository.findAllByTenantIdAndDeletedFalseOrderBySortOrderAscNameAsc(
                        tenantId
                );
        return categoryTreeBuilder.build(categories);
    }

    @Override
    @Transactional
    public CategoryDto moveCategory(
            UUID categoryId,
            MoveCategoryRequest request
    ) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();
        UUID userId = authenticatedUserProvider.getCurrentUserId();

        Category category = categoryProvider.getCategory(categoryId);

        Category newParent = resolveParent(request.newParentId());

        validateCategoryMove(category, newParent);

        category.setParent(newParent);

        Category updatedCategory = categoryRepository.save(category);

        eventPublisher.publishEvent(
                CategoryMovedEvent.of(
                        updatedCategory.getId(),
                        tenantId,
                        userId
                )
        );

        log.info(
                "Category moved. Tenant={}, Category={}, NewParent={}, MovedBy={}",
                tenantId,
                updatedCategory.getId(),
                newParent != null ? newParent.getId() : null,
                userId
        );

        return categoryMapper.toDto(updatedCategory);
    }


    private void validateUniqueCategoryCode(
            String code,
            UUID tenantId
    ) {

        if (categoryRepository.existsByCodeIgnoreCaseAndTenantIdAndDeletedFalse(
                code,
                tenantId
        )) {
            throw new DuplicateResourceException(
                    "Category code '" + code + "' already exists."
            );
        }
    }

    private Category resolveParent(UUID parentId) {

        if (parentId == null) {
            return null;
        }

        return categoryProvider.getCategory(parentId);
    }

    private void validateUniqueSiblingName(
            String name,
            Category parent,
            UUID tenantId
    ) {

        boolean exists;

        if (parent == null) {

            exists = categoryRepository
                    .existsByNameIgnoreCaseAndParentIsNullAndTenantIdAndDeletedFalse(
                            name,
                            tenantId
                    );

        } else {

            exists = categoryRepository
                    .existsByNameIgnoreCaseAndParentIdAndTenantIdAndDeletedFalse(
                            name,
                            parent.getId(),
                            tenantId
                    );
        }

        if (exists) {
            throw new DuplicateResourceException(
                    "A category named '" + name + "' already exists under the selected parent."
            );
        }
    }

    private void validateUniqueSiblingNameForUpdate(
            Category category,
            String newName,
            UUID tenantId
    ) {

        if (newName == null || newName.equalsIgnoreCase(category.getName())) {
            return;
        }

        boolean exists;

        if (category.getParent() == null) {

            exists = categoryRepository
                    .existsByNameIgnoreCaseAndParentIsNullAndTenantIdAndDeletedFalse(
                            newName,
                            tenantId
                    );

        } else {

            exists = categoryRepository
                    .existsByNameIgnoreCaseAndParentIdAndTenantIdAndDeletedFalse(
                            newName,
                            category.getParent().getId(),
                            tenantId
                    );
        }

        if (exists) {
            throw new DuplicateResourceException(
                    "A category named '" + newName + "' already exists under the selected parent."
            );
        }
    }

    private void validateCategoryDeletion(Category category) {

        if (categoryRepository.existsByParentIdAndTenantIdAndDeletedFalse(
                category.getId(),
                category.getTenantId()
        )) {
            throw new BusinessRuleException(
                    "Cannot delete a category that has child categories."
            );
        }

        // Later (Inventory module)
        // if (productRepository.existsByCategoryId(...)) {
        //     throw new BusinessRuleException(
        //         "Cannot delete a category that contains products."
        //     );
        // }
    }

    private void validateCategoryMove(
            Category category,
            Category newParent
    ) {

        if (newParent == null) {
            return;
        }

        if (category.getId().equals(newParent.getId())) {
            throw new BusinessRuleException(
                    "A category cannot be its own parent."
            );
        }

        validateCircularHierarchy(category, newParent);

        validateHierarchyDepth(newParent);
    }
    private void validateCircularHierarchy(
            Category category,
            Category newParent
    ) {

        Category current = newParent;

        while (current != null) {

            if (current.getId().equals(category.getId())) {
                throw new BusinessRuleException(
                        "Cannot move a category beneath one of its descendants."
                );
            }

            current = current.getParent();
        }
    }

    private void validateHierarchyDepth(Category parent) {

        int depth = 0;

        Category current = parent;

        while (current != null) {
            depth++;
            current = current.getParent();
        }

        if (depth >= 4) {
            throw new BusinessRuleException(
                    "Maximum category hierarchy depth exceeded."
            );
        }
    }
}