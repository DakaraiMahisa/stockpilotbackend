package com.stockpilot.backend.catalog.permissions;

public final class CatalogPricingPermissions {

    private CatalogPricingPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "catalog-pricing:read";

    public static final String CREATE = "catalog-pricing:create";

    public static final String UPDATE = "catalog-pricing:update";

    public static final String DELETE = "catalog-pricing:delete";
}