package com.stockpilot.backend.identity.domain.entity;

import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.UUID;


@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"tenant_id", "email"},
                        name = "uk_users_tenant_email"
                )
        },
        indexes = {
                @Index(name = "idx_users_tenant_id", columnList = "tenant_id")
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

    @Column(name = "email_verified", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "mfa_enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean mfaEnabled = false;

    @Column(name = "last_login_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime lastLoginAt;
}

