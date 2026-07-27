package com.stockpilot.backend.identity.usermanagement.permissions;

public final class SessionPermissions {

    private SessionPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "sessions:read";

    public static final String REVOKE = "sessions:revoke";
}