package com.stockpilot.backend.identity.domain.repository;

import com.stockpilot.backend.identity.domain.entity.Role;
import com.stockpilot.backend.identity.domain.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {



    @Query("""
    SELECT p.code
    FROM Role r
    JOIN r.permissions p
    WHERE r.id = :roleId
      AND r.tenantId = :tenantId
      AND r.deleted = false
    """)
    Set<String> findPermissionsByRoleIdAndTenantId(
            @Param("roleId") UUID roleId,
            @Param("tenantId") UUID tenantId
    );


    Optional<Role> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<Role> findByNameAndTenantId(RoleName name, UUID tenantId);
    Optional<Role> findById(UUID id);
    List<Role> findByTenantId(UUID tenantId);
    List<Role> findAllByName(RoleName name);
}

