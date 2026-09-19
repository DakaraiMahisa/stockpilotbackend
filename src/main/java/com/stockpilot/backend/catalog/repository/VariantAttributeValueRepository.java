package com.stockpilot.backend.catalog.repository;

import com.stockpilot.backend.catalog.entity.VariantAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VariantAttributeValueRepository
        extends JpaRepository<VariantAttributeValue, UUID> {

    Optional<VariantAttributeValue> findByIdAndTenantId(
            UUID id,
            UUID tenantId
    );

    Optional<VariantAttributeValue> findByAttributeIdAndCodeAndTenantId(
            UUID attributeId,
            String code,
            UUID tenantId
    );

    boolean existsByAttributeIdAndCodeAndTenantId(
            UUID attributeId,
            String code,
            UUID tenantId
    );

    List<VariantAttributeValue>
    findAllByAttributeIdAndTenantIdAndActiveTrueOrderBySortOrderAsc(
            UUID attributeId,
            UUID tenantId
    );
}