package com.stockpilot.backend.catalog.permissions;

public final class CategoryPermissions {
    private CategoryPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "category:read";

    public static final String CREATE = "category:create";

    public static final String UPDATE = "category:update";

    public static final String MOVE = "category:move";

    public static final String DELETE = "category:delete";

}
