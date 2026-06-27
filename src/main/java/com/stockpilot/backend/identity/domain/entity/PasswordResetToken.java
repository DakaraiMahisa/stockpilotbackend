package com.stockpilot.backend.identity.domain.entity;

import com.stockpilot.backend.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken extends BaseEntity {

    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @Column(name = "used", nullable = false)
    @Builder.Default
    private boolean used = false;

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }
}
