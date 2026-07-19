package com.stockpilot.backend.org.dto.request;

import com.stockpilot.backend.org.enums.UpgradePlan;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SubscriptionUpgradeRequest(

        @NotNull
        UpgradePlan requestedPlan,

        @Size(max = 500, message = "Notes must not exceed 500 characters")
        String notes

) {
}