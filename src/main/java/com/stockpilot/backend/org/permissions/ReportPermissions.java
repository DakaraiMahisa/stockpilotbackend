package com.stockpilot.backend.org.permissions;

public final class ReportPermissions {
    private ReportPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "reports:read";
}
