package com.stockpilot.backend.identity.usermanagement.dto;

import com.stockpilot.backend.identity.usermanagement.enums.UserStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record UserDetailsDto(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String role,
        Set<String> permissions,
        UserStatus status,
        Boolean active,
        Boolean emailVerified,
        Boolean mfaEnabled,
        OffsetDateTime lastLoginAt,
        LocalDateTime createdAt
) {
}