package com.stockpilot.backend.org.permissions;

public final class SupplierPermissions {

    private SupplierPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "suppliers:read";

    public static final String CREATE = "suppliers:create";

    public static final String UPDATE = "suppliers:update";

    public static final String DELETE = "suppliers:delete";
}