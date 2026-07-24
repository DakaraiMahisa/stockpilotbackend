package com.stockpilot.backend.org.permissions;

public final class SubscriptionPermissions {
    private SubscriptionPermissions () {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "subscription:read";

    public static final String UPGRADE = "subscription:upgrade";

}
