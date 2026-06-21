package com.stockpilot.backend.identity.audits.aspect;

import com.stockpilot.backend.identity.audits.annotations.Auditable;
import com.stockpilot.backend.identity.audits.context.AuditMetadataContext;
import com.stockpilot.backend.identity.audits.context.RequestAuditContext;
import com.stockpilot.backend.identity.audits.events.AuditEvent;
import com.stockpilot.backend.identity.audits.publisher.AuditEventPublisher;
import com.stockpilot.backend.shared.utils.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final CurrentUserContext currentUserContext;
    private final RequestAuditContext requestAuditContext;
    private final AuditEventPublisher auditEventPublisher;

    @Around("@annotation(auditable)")
    public Object audit(
            ProceedingJoinPoint joinPoint,
            Auditable auditable
    ) throws Throwable {

        try {

            Object result = joinPoint.proceed();

            Map<String, Object> metadata =
                    AuditMetadataContext.get();

            UUID targetId = (UUID) metadata.get("targetId");

            AuditEvent event = new AuditEvent(
                    UUID.randomUUID(),
                    currentUserContext.getCurrentUserId(),
                    currentUserContext.getCurrentTenantId(),
                    auditable.action(),
                    auditable.severity(),
                    auditable.target(),
                    targetId,
                    metadata,
                    requestAuditContext.getClientIp(),
                    requestAuditContext.getUserAgent(),
                    Instant.now()
            );

            auditEventPublisher.publish(event);

            return result;

        } finally {

            AuditMetadataContext.clear();
        }
    }
}
