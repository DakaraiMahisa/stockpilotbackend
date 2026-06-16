package com.stockpilot.backend.identity.usermanagement.entity;

import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
@Entity
@Table(
        name = "invitation_tokens",
        indexes = {
                @Index(name = "idx_invite_token", columnList = "token_hash"),
                @Index(name = "idx_invite_user", columnList = "user_id")
        }
)
public class InvitationToken extends TenantAwareEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used = false;

    @Column(name = "used_at")
    private Instant usedAt;
}
