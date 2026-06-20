package com.stockpilot.backend.identity.audits.consumer;

import com.stockpilot.backend.identity.audits.config.AuditRabbitConfig;
import com.stockpilot.backend.identity.audits.entity.AuditEventEntity;
import com.stockpilot.backend.identity.audits.events.AuditEvent;
import com.stockpilot.backend.identity.audits.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditEventRepository repository;

    @RabbitListener(
            queues = AuditRabbitConfig.AUDIT_QUEUE
    )
    @Transactional
    public void consume(AuditEvent event) {

        AuditEventEntity entity =
                AuditEventEntity.builder()
                        .id(event.id())
                        .actorId(event.actorId())
                        .tenantId(event.tenantId())
                        .action(event.action())
                        .severity(event.severity())
                        .targetEntity(event.targetEntity())
                        .targetId(event.targetId())
                        .metadata(event.metadata())
                        .ipAddress(event.ipAddress())
                        .userAgent(event.userAgent())
                        .timestamp(event.timestamp())
                        .build();

        repository.save(entity);
    }
}
