package com.stockpilot.backend.inventory.permissions;

public final class InventoryPermissions {

    private InventoryPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String STOCK_READ = "inventory:stock:read";

    public static final String WAREHOUSE_READ = "inventory:warehouse:read";
    public static final String WAREHOUSE_WRITE = "inventory:warehouse:write";

    public static final String BIN_READ = "inventory:bin:read";
    public static final String BIN_WRITE = "inventory:bin:write";
}