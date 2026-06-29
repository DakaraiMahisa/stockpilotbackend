package com.stockpilot.backend.org.repository;


import com.stockpilot.backend.org.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findByTenantId(UUID tenantId);

    boolean existsByTenantId(UUID tenantId);
}