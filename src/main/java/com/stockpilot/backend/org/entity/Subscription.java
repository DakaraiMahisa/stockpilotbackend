package com.stockpilot.backend.org.entity;

import com.stockpilot.backend.org.enums.PlanCode;
import com.stockpilot.backend.org.enums.SubscriptionStatus;
import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(
        name = "subscriptions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "tenant_id")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription extends TenantAwareEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlanCode planCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status;

    @Column(nullable = false)
    private Instant planStartedAt;

    private Instant planExpiresAt;

    private Instant trialEndsAt;

    @Column(nullable = false)
    private Integer maxUsers;

    @Column(nullable = false)
    private Integer maxBranches;

    @Column(nullable = false)
    private Integer maxSkus;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}