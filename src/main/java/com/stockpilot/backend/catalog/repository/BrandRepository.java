package com.stockpilot.backend.catalog.repository;

import com.stockpilot.backend.catalog.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID>, JpaSpecificationExecutor<Brand> {
    Optional<Brand> findByIdAndTenantId(UUID id, UUID tenantId);

    Page<Brand> findAllByTenantId(UUID tenantId, Pageable pageable);

    Page<Brand> findAllByTenantIdAndActive(UUID tenantId, boolean active, Pageable pageable);


    boolean existsByTenantIdAndNameIgnoreCase(UUID tenantId, String name);

    boolean existsByTenantIdAndCodeIgnoreCase(UUID tenantId, String code);

    boolean existsByTenantIdAndNameIgnoreCaseAndIdNot(
            UUID tenantId,
            String name,
            UUID id
    );

    boolean existsByTenantIdAndCodeIgnoreCaseAndIdNot(
            UUID tenantId,
            String code,
            UUID id
    );

    List<Brand> findAllByTenantIdAndActiveTrueOrderByNameAsc(UUID tenantId);
    Optional<Brand> findByTenantIdAndCodeIgnoreCase(UUID tenantId, String code);

    Optional<Brand> findByTenantIdAndNameIgnoreCase(UUID tenantId, String name);
    Optional<Brand> findByIdAndTenantIdAndDeletedFalse(UUID brandId, UUID tenantId);
}