package com.stockpilot.backend.shared.security.permissions;

public final class ReportPermissions {
    private ReportPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "reports:read";
}
