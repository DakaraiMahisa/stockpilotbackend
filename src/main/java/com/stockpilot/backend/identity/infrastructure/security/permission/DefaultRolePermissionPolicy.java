package com.stockpilot.backend.identity.infrastructure.security.permission;


import com.stockpilot.backend.identity.domain.entity.Permission;
import com.stockpilot.backend.identity.domain.enums.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DefaultRolePermissionPolicy {

    /**
     * Returns the default permission set for a newly provisioned role.
     *
     * This policy is applied only during tenant provisioning.
     * Existing tenants are updated separately by RolePermissionSynchronizer.
     */
    public Set<Permission> getPermissionsForRole(
            RoleName roleName,
            List<Permission> allPermissions
    ) {

        return switch (roleName) {

            case OWNER ->
                    new HashSet<>(allPermissions);

            case MANAGER ->
                    filterByCodes(
                            allPermissions,
                            managerPermissionCodes()
                    );

            case CASHIER ->
                    filterByCodes(
                            allPermissions,
                            cashierPermissionCodes()
                    );

            case INVENTORY_CLERK ->
                    filterByCodes(
                            allPermissions,
                            inventoryClerkPermissionCodes()
                    );

            case ACCOUNTANT ->
                    filterByCodes(
                            allPermissions,
                            accountantPermissionCodes()
                    );

            case EMPLOYEE ->
                    filterByCodes(
                            allPermissions,
                            employeePermissionCodes()
                    );
        };
    }

    private Set<Permission> filterByCodes(
            List<Permission> permissions,
            Set<String> codes
    ) {

        return permissions.stream()
                .filter(permission -> codes.contains(permission.getCode()))
                .collect(Collectors.toSet());
    }

    /**
     * Manager default permissions.
     *
     * Expand this list whenever new features are introduced.
     */
    private Set<String> managerPermissionCodes() {

        return Set.of(

                "organization:read",

                "users:read",

                "inventory:read",
                "inventory:create",
                "inventory:update",

                "suppliers:read",
                "suppliers:create",
                "suppliers:update",

                "categories:read",
                "categories:create",
                "categories:update",

                "reports:read"
        );
    }

    /**
     * Employee default permissions.
     */
    private Set<String> employeePermissionCodes() {

        return Set.of(

                "organization:read",

                "inventory:read",

                "suppliers:read",

                "categories:read"
        );
    }
    private Set<String> cashierPermissionCodes() {
        return Set.of(

                "organization:read",

                "sales:read",
                "sales:create",
                "customers:read"
        );
    }

    private Set<String> inventoryClerkPermissionCodes() {
        return Set.of(

                "organization:read",

                "inventory:read",
                "inventory:update",
                "inventory:create",
                "suppliers:read"
        );
    }

    private Set<String> accountantPermissionCodes() {
        return Set.of(

                "organization:read",

                "reports:read",
                "finance:read"
        );
    }
}
