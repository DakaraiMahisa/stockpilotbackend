package com.stockpilot.backend.identity.audits.listeners;

import com.stockpilot.backend.identity.audits.context.RequestAuditContext;
import com.stockpilot.backend.identity.audits.enums.AuditAction;
import com.stockpilot.backend.identity.audits.enums.AuditSeverity;
import com.stockpilot.backend.identity.audits.enums.AuditTargetEntity;
import com.stockpilot.backend.identity.audits.events.AuditEvent;
import com.stockpilot.backend.identity.audits.publisher.AuditEventPublisher;
import com.stockpilot.backend.identity.audits.events.LoginSuccessEvent;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginSuccessAuditListener {

    private final AuditEventPublisher auditEventPublisher;
    private final RequestAuditContext requestContext;

    @EventListener
    public void handle(LoginSuccessEvent event) {

        AuditEvent auditEvent = new AuditEvent(
                UUID.randomUUID(),
                event.getPrincipal().getId(),
                event.getPrincipal().getTenantId(),
                AuditAction.LOGIN_SUCCESS,
                AuditSeverity.INFO,
                AuditTargetEntity.USER,
                event.getPrincipal().getId(),
                Map.of(),
                requestContext.getClientIp(),
                event.getUserAgent(),
                Instant.now()
        );

        auditEventPublisher.publish(auditEvent);
    }
}
