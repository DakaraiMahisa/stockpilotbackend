package com.stockpilot.backend.identity.audits.listeners;

import com.stockpilot.backend.identity.audits.enums.AuditAction;
import com.stockpilot.backend.identity.audits.enums.AuditSeverity;
import com.stockpilot.backend.identity.audits.enums.AuditTargetEntity;
import com.stockpilot.backend.identity.audits.events.AuditEvent;
import com.stockpilot.backend.identity.audits.events.TokenRotatedEvent;
import com.stockpilot.backend.identity.audits.publisher.AuditEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenRotatedAuditListener {

    private final AuditEventPublisher auditEventPublisher;

    @EventListener
    public void handle(TokenRotatedEvent event) {

        AuditEvent auditEvent = new AuditEvent(
                UUID.randomUUID(),
                event.getUserId(),
                event.getTenantId(),
                AuditAction.TOKEN_ROTATED,
                AuditSeverity.INFO,
                AuditTargetEntity.USER,
                event.getUserId(),
                Map.of(),
                event.getIpAddress(),
                event.getUserAgent(),
                Instant.now()
        );

        auditEventPublisher.publish(auditEvent);
    }
}
