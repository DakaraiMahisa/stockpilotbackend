package com.stockpilot.backend.org.exception;

import com.stockpilot.backend.identity.exception.InvalidOperationException;

public class InvalidTaxRateException extends InvalidOperationException {
    public InvalidTaxRateException(String message) {
        super(message);
    }
}
