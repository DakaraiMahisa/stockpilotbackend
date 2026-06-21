package com.stockpilot.backend.identity.audits.listeners;

import com.stockpilot.backend.identity.audits.enums.AuditAction;
import com.stockpilot.backend.identity.audits.enums.AuditSeverity;
import com.stockpilot.backend.identity.audits.enums.AuditTargetEntity;
import com.stockpilot.backend.identity.audits.events.AuditEvent;
import com.stockpilot.backend.identity.audits.events.LoginFailedEvent;
import com.stockpilot.backend.identity.audits.publisher.AuditEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginFailedAuditListener {

    private final AuditEventPublisher auditEventPublisher;

    @EventListener
    public void handle(LoginFailedEvent event) {

        AuditEvent auditEvent = new AuditEvent(
                UUID.randomUUID(),
                null,
                null,
                AuditAction.LOGIN_FAILED,
                AuditSeverity.WARNING,
                AuditTargetEntity.USER,
                null,
                Map.of(
                        "email", event.getEmail(),
                        "tenantCode", event.getTenantCode(),
                        "reason", event.getReason()
                ),
                event.getIpAddress(),
                event.getUserAgent(),
                Instant.now()
        );

        auditEventPublisher.publish(auditEvent);
    }
}
