package com.stockpilot.backend.catalog.permissions;

public final class BrandPermissions {

    private BrandPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "brand:read";

    public static final String CREATE = "brand:create";

    public static final String UPDATE = "brand:update";

    public static final String DELETE = "brand:delete";

}
