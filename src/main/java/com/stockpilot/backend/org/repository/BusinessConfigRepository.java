package com.stockpilot.backend.org.repository;

import com.stockpilot.backend.org.entity.BusinessConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BusinessConfigRepository extends JpaRepository<BusinessConfig, UUID> {

    Optional<BusinessConfig> findByTenantId(UUID tenantId);

    Optional<BusinessConfig> findByOrganizationId(UUID organizationId);

    boolean existsByTenantId(UUID tenantId);
}