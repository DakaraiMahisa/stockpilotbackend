package com.stockpilot.backend.org.permissions;


public final class RolePermissions {

    private RolePermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "roles:read";

    public static final String UPDATE = "roles:update";
}