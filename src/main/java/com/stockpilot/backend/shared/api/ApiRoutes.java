package com.stockpilot.backend.shared.api;

public final class ApiRoutes {

    private ApiRoutes() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String API_V1 = "/v1";

    public static final String AUTH = API_V1 + "/auth";
    public static final String USERS = API_V1 + "/users";
    public static final String ROLES = API_V1 + "/roles";


    public static final String ORGANIZATIONS = API_V1 + "/org";
    public static final String BRANCHES = ORGANIZATIONS + "/branches";
    public static final String BUSINESS_CONFIG = ORGANIZATIONS + "/config";
    public static final String TAXES = API_V1 + "/taxes";
    public static final String SUBSCRIPTIONS = API_V1 + "/subscriptions";
    public static final String ORG_SETTINGS = API_V1 + "/org-settings";
}
