package com.stockpilot.backend.catalog.repository;

import com.stockpilot.backend.catalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByIdAndTenantIdAndDeletedFalse(UUID id, UUID tenantId);

    Optional<Category> findByCodeIgnoreCaseAndTenantIdAndDeletedFalse(String code, UUID tenantId);

    Optional<Category> findByNameIgnoreCaseAndParentIdAndTenantIdAndDeletedFalse(
            String name,
            UUID parentId,
            UUID tenantId
    );

    List<Category> findAllByTenantIdAndDeletedFalseOrderBySortOrderAscNameAsc(UUID tenantId);

    List<Category> findAllByParentIdAndTenantIdAndDeletedFalseOrderBySortOrderAscNameAsc(
            UUID parentId,
            UUID tenantId
    );

    boolean existsByCodeIgnoreCaseAndTenantIdAndDeletedFalse(
            String code,
            UUID tenantId
    );

    boolean existsByNameIgnoreCaseAndParentIdAndTenantIdAndDeletedFalse(
            String name,
            UUID parentId,
            UUID tenantId
    );

    boolean existsByParentIdAndTenantIdAndDeletedFalse(
            UUID parentId,
            UUID tenantId
    );

    boolean existsByNameIgnoreCaseAndParentIsNullAndTenantIdAndDeletedFalse(String name, UUID tenantId);
}