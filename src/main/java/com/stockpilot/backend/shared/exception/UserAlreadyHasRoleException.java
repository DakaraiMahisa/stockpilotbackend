package com.stockpilot.backend.shared.exception;

public class UserAlreadyHasRoleException extends RuntimeException {

    public UserAlreadyHasRoleException() {
        super("User already has the selected role");
    }
}