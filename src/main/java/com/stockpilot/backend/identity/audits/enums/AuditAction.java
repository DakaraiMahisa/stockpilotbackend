package com.stockpilot.backend.identity.audits.enums;

public enum AuditAction {

    LOGIN_SUCCESS,

    LOGIN_FAILED,

    PASSWORD_RESET,

    USER_DEACTIVATED,

    ROLE_CHANGED,

    TOKEN_ROTATED,

    SESSION_REVOKED,

    USER_INVITED,

    USER_ACTIVATED,

    INVITATION_ACCEPTED,

    ACCOUNT_LOCKED
}