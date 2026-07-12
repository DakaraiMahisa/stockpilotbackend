package com.stockpilot.backend.shared.security.permissions;

public final class TaxConfigPermissions {

    private TaxConfigPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "tax:read";

    public static final String CREATE = "tax:create";

    public static final String UPDATE = "tax:update";

    public static final String RESOLVE = "tax:resolve";

    public static final String SET_DEFAULT = "tax:set-default";
}
