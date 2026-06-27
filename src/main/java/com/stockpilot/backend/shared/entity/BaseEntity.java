package com.stockpilot.backend.shared.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("deleted = false")
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            columnDefinition = "uuid DEFAULT gen_random_uuid()",
            nullable = false,
            updatable = false
    )
    private UUID id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;
}
