package com.stockpilot.backend.shared.exception;

import java.util.UUID;

public class OrganizationNotFoundException extends ResourceNotFoundException {

    public OrganizationNotFoundException(UUID tenantId) {
        super("Organization not found for tenant: " + tenantId);
    }
}
