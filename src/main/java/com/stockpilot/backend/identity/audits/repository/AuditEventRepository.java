package com.stockpilot.backend.identity.audits.repository;

import com.stockpilot.backend.identity.audits.entity.AuditEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditEventRepository
        extends JpaRepository<AuditEventEntity, UUID> {
}
