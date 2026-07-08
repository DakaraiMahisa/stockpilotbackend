package com.stockpilot.backend.identity.exception;

public class UserAlreadyActiveException extends RuntimeException {

    public UserAlreadyActiveException() {
        super("User account is already active");
    }
}
