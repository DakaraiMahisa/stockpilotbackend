package com.stockpilot.backend.org.permissions;

public final  class CustomerPermissions {
    private CustomerPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "customers:read";
    public static final String CREATE = "customers:create";
    public static final String UPDATE = "customers:update";
    public static final String DELETE = "customers:delete";
}
