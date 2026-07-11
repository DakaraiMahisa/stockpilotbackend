package com.stockpilot.backend.shared.security.permissions;

public final class BusinessConfigPermissions {

    private BusinessConfigPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "business-config:read";

    public static final String UPDATE = "business-config:update";
}