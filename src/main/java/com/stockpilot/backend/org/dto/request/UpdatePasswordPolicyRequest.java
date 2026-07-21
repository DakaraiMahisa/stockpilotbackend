package com.stockpilot.backend.org.dto.request;

import com.stockpilot.backend.shared.validation.annotation.ValidPasswordExpiryDays;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UpdatePasswordPolicyRequest(

        @Min(8)
        @Max(32)
        Integer minPasswordLength,

        Boolean requireUppercase,

        Boolean requireNumber,

        Boolean requireSpecialChar,

        @ValidPasswordExpiryDays
        Integer passwordExpiryDays,

        @Min(3)
        @Max(10)
        Integer maxLoginAttempts,

        @Min(5)
        @Max(1440)
        Integer lockoutDurationMins

) {
}
