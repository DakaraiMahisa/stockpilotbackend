package com.stockpilot.backend.identity.usermanagement.dto;

import com.stockpilot.backend.identity.usermanagement.enums.UserStatus;

import java.time.Instant;
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
        Instant lastLoginAt,
        Instant createdAt
) {
}