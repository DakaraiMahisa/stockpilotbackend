package com.stockpilot.backend.org.event;


import com.stockpilot.backend.org.enums.UpgradePlan;

import java.util.UUID;

public record UpgradeRequestedEvent(

        UUID requestId,

        UUID tenantId,

        UpgradePlan requestedPlan,

        String notes

) {
}
