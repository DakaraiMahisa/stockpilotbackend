package com.stockpilot.backend.identity.usermanagement.dto;

import com.stockpilot.backend.identity.usermanagement.enums.UserStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserSummaryDto(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String role,
        UserStatus status,
        Boolean active,
        OffsetDateTime lastLoginAt
) {
}
