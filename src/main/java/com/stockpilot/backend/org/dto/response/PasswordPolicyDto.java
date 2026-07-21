package com.stockpilot.backend.org.dto.response;

import lombok.Builder;

@Builder
public record PasswordPolicyDto(

        Integer minPasswordLength,

        Boolean requireUppercase,

        Boolean requireNumber,

        Boolean requireSpecialChar,

        Integer passwordExpiryDays,

        Integer maxLoginAttempts,

        Integer lockoutDurationMins

) {
}
