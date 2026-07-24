package com.stockpilot.backend.org.permissions;

public final class FinancePermissions {
    private FinancePermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "finance:read";
}
