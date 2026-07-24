package com.stockpilot.backend.org.permissions;

public final class BranchPermissions {

    private BranchPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "branches:read";

    public static final String CREATE = "branches:create";

    public static final String UPDATE = "branches:update";

    public static final String UPDATE_STATUS = "branches:update-status";

    public static final String SET_DEFAULT = "branches:set-default";
}