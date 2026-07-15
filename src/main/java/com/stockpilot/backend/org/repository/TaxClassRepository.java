package com.stockpilot.backend.org.repository;

import com.stockpilot.backend.org.entity.TaxClass;
import com.stockpilot.backend.org.enums.TaxType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaxClassRepository extends JpaRepository<TaxClass, UUID> {

    List<TaxClass> findByTenantIdAndDeletedFalseOrderByNameAsc(UUID tenantId);

    List<TaxClass> findByTenantIdAndTaxTypeAndDeletedFalseOrderByNameAsc(
            UUID tenantId,
            TaxType taxType
    );

    Optional<TaxClass> findByIdAndTenantIdAndDeletedFalse(
            UUID id,
            UUID tenantId
    );

    Optional<TaxClass> findByTenantIdAndDefaultTaxClassTrueAndDeletedFalse(
            UUID tenantId
    );

    boolean existsByTenantIdAndNameIgnoreCaseAndDeletedFalse(
            UUID tenantId,
            String name
    );

    boolean existsByTenantIdAndCodeIgnoreCaseAndDeletedFalse(
            UUID tenantId,
            String code
    );

    boolean existsByTenantIdAndDeletedFalse(UUID tenantId);

    boolean existsByTenantIdAndNameIgnoreCaseAndDeletedFalseAndIdNot(
            UUID tenantId,
            String name,
            UUID id
    );

    Optional<TaxClass> findByTenantIdAndNameIgnoreCaseAndDeletedFalse(
            UUID tenantId,
            String name
    );
}
