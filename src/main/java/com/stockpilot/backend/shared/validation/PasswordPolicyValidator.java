package com.stockpilot.backend.shared.validation;

import java.util.UUID;

public interface PasswordPolicyValidator {

    void validate(
            String rawPassword,
            UUID tenantId
    );
}