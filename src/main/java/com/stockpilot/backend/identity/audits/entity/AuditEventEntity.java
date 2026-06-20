package com.stockpilot.backend.identity.audits.entity;

import com.stockpilot.backend.identity.audits.enums.AuditAction;
import com.stockpilot.backend.identity.audits.enums.AuditSeverity;
import com.stockpilot.backend.identity.audits.enums.AuditTargetEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "audit_events",
        indexes = {
                @Index(name = "idx_audit_tenant", columnList = "tenant_id"),
                @Index(name = "idx_audit_actor", columnList = "actor_id"),
                @Index(name = "idx_audit_action", columnList = "action"),
                @Index(name = "idx_audit_timestamp", columnList = "timestamp")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEventEntity {

    @Id
    private UUID id;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_entity", nullable = false)
    private AuditTargetEntity targetEntity;

    @Column(name = "target_id")
    private UUID targetId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(nullable = false)
    private Instant timestamp;
}
