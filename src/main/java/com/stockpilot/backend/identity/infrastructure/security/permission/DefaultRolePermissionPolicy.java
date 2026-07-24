package com.stockpilot.backend.identity.infrastructure.security.permission;


import com.stockpilot.backend.catalog.permissions.CategoryPermissions;
import com.stockpilot.backend.identity.domain.entity.Permission;
import com.stockpilot.backend.identity.domain.enums.RoleName;
import com.stockpilot.backend.org.permissions.*;
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

                OrganizationPermissions.READ,

                BranchPermissions.READ,
                BranchPermissions.UPDATE,

                BusinessConfigPermissions.READ,

                TaxConfigPermissions.READ,
                TaxConfigPermissions.CREATE,
                TaxConfigPermissions.UPDATE,
                TaxConfigPermissions.RESOLVE,

                UserPermissions.READ,

                InventoryPermissions.READ,
                InventoryPermissions.UPDATE,
                InventoryPermissions.CREATE,

                SupplierPermissions.READ,
                SupplierPermissions.CREATE,
                SupplierPermissions.UPDATE,

                CategoryPermissions.READ,
                CategoryPermissions.CREATE,
                CategoryPermissions.UPDATE,

                ReportPermissions.READ
        );
    }

    /**
     * Employee default permissions.
     */
    private Set<String> employeePermissionCodes() {

        return Set.of(

                OrganizationPermissions.READ,

                BusinessConfigPermissions.READ,

                TaxConfigPermissions.READ,

                CategoryPermissions.READ,

                InventoryPermissions.READ,

                SupplierPermissions.READ,

                CategoryPermissions.READ
        );
    }
    private Set<String> cashierPermissionCodes() {
        return Set.of(

                OrganizationPermissions.READ,

                BusinessConfigPermissions.READ,

                TaxConfigPermissions.READ,
                TaxConfigPermissions.RESOLVE,

                CategoryPermissions.READ,
                SalesPermissions.READ,
                SalesPermissions.CREATE,
                CustomerPermissions.READ
        );
    }

    private Set<String> inventoryClerkPermissionCodes() {
        return Set.of(

                OrganizationPermissions.READ,

                BusinessConfigPermissions.READ,

                TaxConfigPermissions.READ,

                CategoryPermissions.READ,

                InventoryPermissions.READ,
                InventoryPermissions.UPDATE,
                InventoryPermissions.CREATE,

                SupplierPermissions.READ
        );
    }

    private Set<String> accountantPermissionCodes() {
        return Set.of(

                OrganizationPermissions.READ,

                BusinessConfigPermissions.READ,
                TaxConfigPermissions.READ,

                CategoryPermissions.READ,
                ReportPermissions.READ,
                FinancePermissions.READ
        );
    }
}
