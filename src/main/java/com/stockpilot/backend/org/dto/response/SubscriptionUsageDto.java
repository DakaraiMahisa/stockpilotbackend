package com.stockpilot.backend.org.dto.response;

import lombok.Builder;

@Builder
public record SubscriptionUsageDto(

        long users,

        long branches,

        long skus

) {
}