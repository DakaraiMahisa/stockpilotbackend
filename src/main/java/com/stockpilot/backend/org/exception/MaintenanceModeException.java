package com.stockpilot.backend.org.exception;

public class MaintenanceModeException
        extends RuntimeException {

    public MaintenanceModeException(
            String message
    ) {
        super(message);
    }
}