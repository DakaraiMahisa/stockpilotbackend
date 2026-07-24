package com.stockpilot.backend.org.permissions;

public final class SalesPermissions {

    private SalesPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "sales:read";

    public static final String CREATE = "sales:create";

    public static final String UPDATE = "sales:update";

    public static final String REFUND = "sales:refund";
}