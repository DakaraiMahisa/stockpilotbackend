package com.stockpilot.backend.org.repository;

import com.stockpilot.backend.org.entity.OrgSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrgSettingsRepository extends JpaRepository<OrgSettings, UUID> {

    Optional<OrgSettings> findByTenantId(UUID tenantId);

    Optional<OrgSettings> findByOrganizationId(UUID organizationId);

    boolean existsByTenantId(UUID tenantId);
}