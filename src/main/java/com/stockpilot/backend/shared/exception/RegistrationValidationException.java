package com.stockpilot.backend.shared.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class RegistrationValidationException extends BusinessException {
    private final Map<String, String> errors;

    public RegistrationValidationException(Map<String, String> errors) {
        super("Registration validation failed");
        this.errors = errors;
    }
}
