package com.stockpilot.backend.org.dto.response;

import lombok.Builder;

@Builder
public record SubscriptionLimitsDto(

        Integer maxUsers,

        Integer maxBranches,

        Integer maxSkus

) {
}