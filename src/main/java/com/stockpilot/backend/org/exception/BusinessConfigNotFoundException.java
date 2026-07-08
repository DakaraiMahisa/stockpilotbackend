package com.stockpilot.backend.org.exception;

import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;

import java.util.UUID;

public class BusinessConfigNotFoundException extends ResourceNotFoundException {

    public BusinessConfigNotFoundException(UUID tenantId) {
        super("Business configuration not found for tenant: " + tenantId);
    }
}