package com.stockpilot.backend.identity.exception;

public class UserAlreadyHasRoleException extends RuntimeException {

    public UserAlreadyHasRoleException() {
        super("User already has the selected role");
    }
}