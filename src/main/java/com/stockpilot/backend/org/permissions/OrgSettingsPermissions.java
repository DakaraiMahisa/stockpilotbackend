package com.stockpilot.backend.org.permissions;

public class OrgSettingsPermissions {

    private OrgSettingsPermissions() {
        throw new IllegalStateException("Utility class");
    }

    public static final String READ = "settings:read";
    public static final String UPDATE_PASSWORD_POLICY = "settings:update-password-policy";
    public static final String UPDATE_SESSION_POLICY = "settings:update-session-policy";
    public static final String UPDATE_INVITE_POLICY = "settings:update-invite-policy";
    public static final String UPDATE_GENERAL = "settings:update-general";

}
