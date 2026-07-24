package com.stockpilot.backend.org.permissions;

public final class InventoryPermissions {

    private InventoryPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "inventory:read";

    public static final String CREATE = "inventory:create";

    public static final String UPDATE = "inventory:update";

    public static final String DELETE = "inventory:delete";
}