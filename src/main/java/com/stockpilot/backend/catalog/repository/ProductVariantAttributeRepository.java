package com.stockpilot.backend.catalog.repository;

import com.stockpilot.backend.catalog.entity.ProductVariantAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductVariantAttributeRepository
        extends JpaRepository<ProductVariantAttribute, UUID> {

    List<ProductVariantAttribute>
    findAllByProductIdAndTenantIdOrderBySortOrderAsc(
            UUID productId,
            UUID tenantId
    );

    Optional<ProductVariantAttribute>
    findByProductIdAndAttributeIdAndTenantId(
            UUID productId,
            UUID attributeId,
            UUID tenantId
    );

    boolean existsByProductIdAndAttributeIdAndTenantId(
            UUID productId,
            UUID attributeId,
            UUID tenantId
    );

    boolean existsByAttributeIdAndTenantId(
            UUID attributeId,
            UUID tenantId
    );

    void deleteByProductIdAndAttributeIdAndTenantId(
            UUID productId,
            UUID attributeId,
            UUID tenantId
    );
}