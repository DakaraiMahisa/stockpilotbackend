package com.stockpilot.backend.catalog.repository;

import com.stockpilot.backend.catalog.entity.VariantAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VariantAttributeRepository
        extends JpaRepository<VariantAttribute, UUID> {

    Optional<VariantAttribute> findByIdAndTenantId(
            UUID id,
            UUID tenantId
    );

    Optional<VariantAttribute> findByCodeAndTenantId(
            String code,
            UUID tenantId
    );
    List<VariantAttribute> findAllByTenantIdOrderByNameAsc(
            UUID tenantId
    );
    boolean existsByCodeAndTenantId(
            String code,
            UUID tenantId
    );

}