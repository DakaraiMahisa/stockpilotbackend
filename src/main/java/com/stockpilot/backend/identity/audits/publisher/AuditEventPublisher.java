package com.stockpilot.backend.identity.audits.publisher;


import com.stockpilot.backend.identity.audits.events.AuditEvent;

public interface AuditEventPublisher {

    void publish(AuditEvent event);
}