package com.stockpilot.backend.shared.security.permissions;

public final class CategoryPermissions {

    private CategoryPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "categories:read";

    public static final String CREATE = "categories:create";

    public static final String UPDATE = "categories:update";

    public static final String DELETE = "categories:delete";
}