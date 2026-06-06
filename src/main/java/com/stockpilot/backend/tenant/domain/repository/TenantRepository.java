package com.stockpilot.backend.tenant.domain.repository;

import com.stockpilot.backend.tenant.domain.entity.Tenant;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findByIdAndDeletedFalse(UUID id);

    Optional<Tenant> findByCodeAndDeletedFalse(String code);

    boolean existsByCode(String code);

    boolean existsByNameIgnoreCase(String name);

    Optional<Tenant> findByCode(@NotBlank(message = "Tenant name is required") String code);
}
