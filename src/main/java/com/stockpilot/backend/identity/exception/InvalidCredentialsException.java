package com.stockpilot.backend.identity.exception;

import com.stockpilot.backend.shared.exception.base.BusinessException;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
