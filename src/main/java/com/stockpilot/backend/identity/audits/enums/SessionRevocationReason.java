package com.stockpilot.backend.identity.audits.enums;

public enum SessionRevocationReason {
    USER_LOGOUT,
    ADMIN_SESSION_REVOKE,
    USER_DEACTIVATED,
    PASSWORD_RESET
}
