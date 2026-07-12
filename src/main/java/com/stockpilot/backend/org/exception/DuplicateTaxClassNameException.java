package com.stockpilot.backend.org.exception;

import com.stockpilot.backend.shared.exception.base.DuplicateResourceException;

public class DuplicateTaxClassNameException extends DuplicateResourceException {
    public DuplicateTaxClassNameException(String message) {
        super(message);
    }
}
