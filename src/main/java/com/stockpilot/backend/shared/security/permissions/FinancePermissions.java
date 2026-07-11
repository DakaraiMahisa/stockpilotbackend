package com.stockpilot.backend.shared.security.permissions;

public final class FinancePermissions {
    private FinancePermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "finance:read";
}
