package com.stockpilot.backend.identity.domain.entity;

import com.stockpilot.backend.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Entity
@Table(name = "verification_tokens")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private OffsetDateTime expiryDate;

    public boolean isExpired() {
        return expiryDate.isBefore(OffsetDateTime.now());
    }
}

