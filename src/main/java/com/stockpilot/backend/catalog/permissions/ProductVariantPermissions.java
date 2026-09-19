package com.stockpilot.backend.catalog.permissions;

public class ProductVariantPermissions {

    private ProductVariantPermissions() {
        throw new IllegalStateException("Utility class");
    }
    public static final String READ = "variant:read";

    public static final String CREATE = "variant:create";

    public static final String UPDATE = "variant:update";

    public static final String DELETE = "variant:delete";

}
