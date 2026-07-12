package com.stockpilot.backend.org.exception;

import com.stockpilot.backend.shared.exception.base.DuplicateResourceException;

public class DuplicateTaxClassCodeException extends DuplicateResourceException {
    public DuplicateTaxClassCodeException(String message) {
        super(message);
    }
}
