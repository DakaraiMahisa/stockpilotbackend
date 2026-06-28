package com.stockpilot.backend.common.builders;

import com.stockpilot.backend.identity.domain.entity.Permission;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PermissionBuilder {


    private UUID id = UUID.randomUUID();

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    private Long version = 0L;

    private boolean deleted = false;

    private String code = "users:read";

    private String description = "Allows viewing users.";

    private Set<?> roles = new HashSet<>();

    private PermissionBuilder() {
    }

    public static PermissionBuilder aPermission() {
        return new PermissionBuilder();
    }

    public static PermissionBuilder usersRead() {
        return aPermission()
                .code("users:read")
                .description("Allows viewing users.");
    }

    public static PermissionBuilder usersInvite() {
        return aPermission()
                .code("users:invite")
                .description("Allows inviting users.");
    }

    public static PermissionBuilder usersUpdate() {
        return aPermission()
                .code("users:update")
                .description("Allows updating users.");
    }

    public PermissionBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public PermissionBuilder code(String code) {
        this.code = code;
        return this;
    }

    public PermissionBuilder description(String description) {
        this.description = description;
        return this;
    }

    public PermissionBuilder createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public PermissionBuilder updatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public PermissionBuilder version(Long version) {
        this.version = version;
        return this;
    }

    public PermissionBuilder deleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public Permission build() {

        return Permission.builder()

                .id(id)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .version(version)
                .deleted(deleted)

                .code(code)
                .description(description)
                .build();
    }
}