package com.stockpilot.backend.shared.exception;

public class UserAlreadyActiveException extends RuntimeException {

    public UserAlreadyActiveException() {
        super("User account is already active");
    }
}
