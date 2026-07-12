package com.stockpilot.backend.org.exception;

import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;



public class TaxClassNotFoundException extends ResourceNotFoundException {
    public TaxClassNotFoundException(String message) {
        super(message);
    }
}
