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
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_rt_token", columnList = "token"),
        @Index(name = "idx_rt_user_id", columnList = "user_id")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private OffsetDateTime expiryDate;

    @Column(name = "device_info", columnDefinition = "TEXT")
    private String deviceInfo;

    public boolean isExpired() {
        return expiryDate.isBefore(OffsetDateTime.now());
    }
}

