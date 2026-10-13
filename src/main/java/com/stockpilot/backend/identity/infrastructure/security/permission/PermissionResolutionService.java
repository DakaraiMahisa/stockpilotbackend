package com.stockpilot.backend.identity.infrastructure.security.permission;


import com.stockpilot.backend.identity.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionResolutionService {

    private final RoleRepository roleRepository;

    public Set<String> resolveForRole(
            UUID tenantId,
            UUID roleId
    ) {
        Objects.requireNonNull(tenantId, "tenantId is required");
        Objects.requireNonNull(roleId, "roleId is required");

        return Set.copyOf(
                roleRepository.findPermissionsByRoleIdAndTenantId(
                        roleId,
                        tenantId
                )
        );
    }
}
