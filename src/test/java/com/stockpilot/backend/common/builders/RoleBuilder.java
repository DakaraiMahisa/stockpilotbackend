package com.stockpilot.backend.common.builders;

import com.stockpilot.backend.identity.domain.entity.Permission;
import com.stockpilot.backend.identity.domain.entity.Role;
import com.stockpilot.backend.identity.domain.enums.RoleName;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class RoleBuilder {


    private UUID id = UUID.randomUUID();

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    private Long version = 0L;

    private boolean deleted = false;

    private UUID tenantId = UUID.randomUUID();


    private RoleName name = RoleName.OWNER;

    private Set<Permission> permissions = new HashSet<>();

    private RoleBuilder() {

        permissions.add(
                PermissionBuilder.usersRead().build()
        );
    }

    public static RoleBuilder aRole() {
        return new RoleBuilder();
    }

    public static RoleBuilder ownerRole() {

        return aRole()
                .name(RoleName.OWNER);
    }

    public static RoleBuilder managerRole() {

        return aRole()
                .name(RoleName.MANAGER);
    }

    public static RoleBuilder cashierRole() {

        return aRole()
                .name(RoleName.CASHIER);
    }

    public RoleBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public RoleBuilder tenantId(UUID tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public RoleBuilder name(RoleName name) {
        this.name = name;
        return this;
    }

    public RoleBuilder permissions(Set<Permission> permissions) {
        this.permissions = permissions;
        return this;
    }

    public RoleBuilder addPermission(Permission permission) {
        this.permissions.add(permission);
        return this;
    }

    public RoleBuilder createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public RoleBuilder updatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public RoleBuilder version(Long version) {
        this.version = version;
        return this;
    }

    public RoleBuilder deleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public Role build() {

        return Role.builder()

                .id(id)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .version(version)
                .deleted(deleted)
                .tenantId(tenantId)
                .name(name)
                .permissions(permissions)
                .build();
    }
}
