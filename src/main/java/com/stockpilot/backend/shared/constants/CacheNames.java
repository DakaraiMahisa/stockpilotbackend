package com.stockpilot.backend.shared.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheNames {

    /**
     * Organization Administration
     */
    public static final String BUSINESS_CONFIG = "businessConfig";
    public static final String ORGANIZATION = "organization";
    public static final String BRANCH = "branch";
    public static final String TAX_CLASS = "taxClass";
    public static final String ORG_SETTINGS = "orgSettings";

    /**
     * Identity & Access Management
     */
    public static final String USER_PERMISSIONS = "userPermissions";
    public static final String USER_SESSIONS = "userSessions";

}