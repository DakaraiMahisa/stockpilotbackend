package com.stockpilot.backend.org.exception;

import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;

import java.util.UUID;

public class OrganizationNotFoundException extends ResourceNotFoundException {

    public OrganizationNotFoundException(UUID tenantId) {
        super("Organization not found for tenant: " + tenantId);
    }
}
