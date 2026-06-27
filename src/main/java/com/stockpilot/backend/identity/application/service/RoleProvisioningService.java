package com.stockpilot.backend.identity.application.service;

import com.stockpilot.backend.identity.domain.entity.Permission;
import com.stockpilot.backend.identity.domain.entity.Role;
import com.stockpilot.backend.identity.domain.enums.RoleName;
import com.stockpilot.backend.identity.domain.repository.PermissionRepository;
import com.stockpilot.backend.identity.domain.repository.RoleRepository;
import com.stockpilot.backend.identity.infrastructure.security.permission.DefaultRolePermissionPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleProvisioningService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final DefaultRolePermissionPolicy permissionPolicy;

    @Transactional
    public void provisionDefaultRoles(UUID tenantId) {

        List<Permission> allPermissions = permissionRepository.findAll();

        if (allPermissions.isEmpty()) {
            throw new IllegalStateException(
                    "Permission catalog is empty. Ensure Flyway permission seed migration has been executed."
            );
        }

        List<Role> roles = Arrays.stream(RoleName.values())
                .map(roleName -> Role.builder()
                        .tenantId(tenantId)
                        .name(roleName)
                        .permissions(
                                permissionPolicy.getPermissionsForRole(
                                        roleName,
                                        allPermissions
                                )
                        )
                        .build())
                .collect(Collectors.toList());

        roleRepository.saveAll(roles);
    }
}