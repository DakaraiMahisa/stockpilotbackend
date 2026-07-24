package com.stockpilot.backend.org.permissions;

public final class OrganizationPermissions {

    private OrganizationPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "organization:read";

    public static final String UPDATE = "organization:update";
}