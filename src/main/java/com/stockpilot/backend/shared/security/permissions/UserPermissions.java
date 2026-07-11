package com.stockpilot.backend.shared.security.permissions;

public final class UserPermissions {

    private UserPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "users:read";

    public static final String CREATE = "users:create";
    public static final String INVITE = "users:invite";

    public static final String UPDATE = "users:update";

    public static final String DELETE = "users:delete";

    public static final String ACTIVATE = "users:activate";

    public static final String DEACTIVATE = "users:deactivate";
}