package com.stockpilot.backend.identity.audits.events;

import com.stockpilot.backend.identity.audits.enums.AuditAction;
import com.stockpilot.backend.identity.audits.enums.AuditSeverity;
import com.stockpilot.backend.identity.audits.enums.AuditTargetEntity;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AuditEvent(

        UUID id,

        UUID actorId,

        UUID tenantId,

        AuditAction action,

        AuditSeverity severity,

        AuditTargetEntity targetEntity,

        UUID targetId,

        Map<String, Object> metadata,

        String ipAddress,

        String userAgent,

        Instant timestamp
) {
}