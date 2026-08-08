package com.stockpilot.backend.catalog.permissions;

public final class ProductPermissions {

    private ProductPermissions() {
        throw new IllegalStateException("Utility class");
    }
    public static final String READ = "product:read";

    public static final String CREATE = "product:create";

    public static final String UPDATE = "product:update";

    public static final String DELETE = "product:delete";

}
