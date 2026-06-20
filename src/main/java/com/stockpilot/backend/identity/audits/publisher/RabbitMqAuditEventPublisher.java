package com.stockpilot.backend.identity.audits.publisher;

import com.stockpilot.backend.identity.audits.config.AuditRabbitConfig;
import com.stockpilot.backend.identity.audits.events.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Primary
public class RabbitMqAuditEventPublisher
        implements AuditEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(AuditEvent event) {

        rabbitTemplate.convertAndSend(
                AuditRabbitConfig.AUDIT_EXCHANGE,
                AuditRabbitConfig.AUDIT_ROUTING_KEY,
                event
        );
    }
}