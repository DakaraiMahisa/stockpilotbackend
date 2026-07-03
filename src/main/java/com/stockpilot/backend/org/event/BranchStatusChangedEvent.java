package com.stockpilot.backend.org.event;

import com.stockpilot.backend.org.enums.BranchStatus;

import java.time.Instant;
import java.util.UUID;

public record BranchStatusChangedEvent(

        UUID branchId,

        UUID tenantId,

        BranchStatus previousStatus,

        BranchStatus currentStatus,

        Instant occurredAt

) {
}