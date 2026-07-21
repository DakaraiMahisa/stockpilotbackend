package com.stockpilot.backend.org.entity;

import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "org_settings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_org_settings_tenant",
                        columnNames = "tenant_id"
                ),
                @UniqueConstraint(
                        name = "uk_org_settings_organization",
                        columnNames = "organization_id"
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OrgSettings extends TenantAwareEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "organization_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_org_settings_organization")
    )
    private Organization organization;

    // ============================================================
    // Password Policy
    // ============================================================

    @Builder.Default
    @Column(nullable = false)
    private Integer minPasswordLength = 8;

    @Builder.Default
    @Column(nullable = false)
    private Boolean requireUppercase = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean requireNumber = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean requireSpecialChar = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer passwordExpiryDays = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer maxLoginAttempts = 5;

    @Builder.Default
    @Column(nullable = false)
    private Integer lockoutDurationMins = 30;

    // ============================================================
    // Session Policy
    // ============================================================

    @Builder.Default
    @Column(nullable = false)
    private Integer sessionTimeoutMins = 60;

    @Builder.Default
    @Column(nullable = false)
    private Integer maxConcurrentSessions = 3;

    @Builder.Default
    @Column(nullable = false)
    private Integer rememberMeDays = 7;

    @Builder.Default
    @Column(nullable = false)
    private Boolean enforceDeviceTrust = false;

    // ============================================================
    // Invitation Policy
    // ============================================================

    @Builder.Default
    @Column(nullable = false)
    private Integer inviteExpiryHours = 48;

    @Builder.Default
    @Column(nullable = false)
    private Boolean allowSelfRegistration = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean requireEmailVerification = true;

    // ============================================================
    // General
    // ============================================================

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String defaultLanguage = "en-US";

    @Builder.Default
    @Column(nullable = false, length = 50)
    private String defaultTimezone = "UTC";

    @Builder.Default
    @Column(nullable = false)
    private Boolean maintenanceMode = false;
}