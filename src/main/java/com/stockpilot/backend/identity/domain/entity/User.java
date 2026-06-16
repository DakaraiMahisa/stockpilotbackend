package com.stockpilot.backend.identity.domain.entity;

import com.stockpilot.backend.identity.usermanagement.enums.UserStatus;
import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_tenant_email",
                        columnNames = {"tenant_id", "email"}
                )
        },
        indexes = {
                @Index(name = "idx_users_tenant_id", columnList = "tenant_id"),
                @Index(name = "idx_users_tenant_role_id", columnList = "tenant_id, role_id"),
                @Index(name = "idx_users_tenant_status", columnList = "tenant_id, status")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends TenantAwareEntity {

    @Column(name = "email", length = 150, nullable = false)
    private String email;

    @Column(name = "password_hash", columnDefinition = "TEXT", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", length = 80, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 80, nullable = false)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20,
            columnDefinition = "VARCHAR(20) DEFAULT 'ACTIVE'"
    )
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "locked", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean locked = false;

    @Column(
            name = "failed_login_attempts",
            nullable = false,
            columnDefinition = "INTEGER DEFAULT 0"
    )
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column(name = "email_verified", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "mfa_enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean mfaEnabled = false;

    @Column(name = "invited_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime invitedAt;

    @Column(name = "last_login_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime lastLoginAt;

    @Column(name = "locked_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime lockedAt;
}

