package com.stockpilot.backend.identity.infrastructure.security.permission;

import com.stockpilot.backend.identity.domain.entity.Permission;
import com.stockpilot.backend.identity.domain.entity.Role;
import com.stockpilot.backend.identity.domain.enums.RoleName;
import com.stockpilot.backend.identity.domain.repository.PermissionRepository;
import com.stockpilot.backend.identity.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolePermissionSynchronizer {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final DefaultRolePermissionPolicy permissionPolicy;

    /**
     * Synchronizes default roles with the latest permission catalog.
     *
     * Existing permissions are preserved.
     * Missing permissions are added.
     * Permissions are never removed.
     *
     * Safe to execute multiple times.
     */
    @Transactional
    public void synchronizeDefaultRoles() {

        List<Permission> permissionCatalog = permissionRepository.findAll();

        if (permissionCatalog.isEmpty()) {
            throw new IllegalStateException(
                    "Permission catalog is empty. Ensure Flyway seed migration has been executed."
            );
        }


        List<Role> roles = roleRepository.findAll();


        List<Role> updatedRoles = new ArrayList<>();

        int permissionsAdded = 0;

        for (Role role : roles) {
            if (role.getName() == null) {
                continue;
            }
            Set<Permission> expectedPermissions =
                    permissionPolicy.getPermissionsForRole(
                            role.getName(),
                            permissionCatalog
                    );

            Set<Permission> currentPermissions = role.getPermissions();

            boolean changed = false;

            Set<String> currentCodes = currentPermissions.stream()
                    .map(Permission::getCode)
                    .collect(Collectors.toSet());

            for (Permission permission : expectedPermissions) {

                if (!currentCodes.contains(permission.getCode())) {

                    currentPermissions.add(permission);

                    currentCodes.add(permission.getCode());

                    permissionsAdded++;

                    changed = true;
                }
            }

            if (changed) {
                updatedRoles.add(role);
            }
        }

        if (!updatedRoles.isEmpty()) {
            roleRepository.saveAll(updatedRoles);
        }

        log.info("""
        Default role synchronization completed.
        Roles scanned : {}
        Roles updated : {}
        Permissions added : {}
        """,
                roles.size(),
                updatedRoles.size(),
                permissionsAdded
        );
    }
}