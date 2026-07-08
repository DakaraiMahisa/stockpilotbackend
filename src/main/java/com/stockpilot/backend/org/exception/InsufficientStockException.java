package com.stockpilot.backend.org.exception;

import com.stockpilot.backend.shared.exception.base.BusinessException;

public class InsufficientStockException extends BusinessException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
}

