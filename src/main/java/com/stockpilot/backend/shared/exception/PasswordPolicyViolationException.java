package com.stockpilot.backend.shared.exception;

public class PasswordPolicyViolationException
        extends RuntimeException {

    public PasswordPolicyViolationException(
            String message
    ) {
        super(message);
    }
}