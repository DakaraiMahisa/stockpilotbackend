package com.stockpilot.backend.org.dto.response;

import com.stockpilot.backend.org.enums.PlanCode;
import com.stockpilot.backend.org.enums.SubscriptionStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record SubscriptionDto(

        PlanCode planCode,

        SubscriptionStatus status,

        Instant planStartedAt,

        Instant trialEndsAt,

        Instant planExpiresAt,

        boolean active,

        SubscriptionUsageDto usage,

        SubscriptionLimitsDto limits

) {
}