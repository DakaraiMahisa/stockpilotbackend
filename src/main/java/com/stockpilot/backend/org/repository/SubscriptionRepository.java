package com.stockpilot.backend.org.repository;

import com.stockpilot.backend.org.entity.Subscription;
import com.stockpilot.backend.org.enums.PlanCode;
import com.stockpilot.backend.org.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByTenantId(UUID tenantId);

    boolean existsByTenantId(UUID tenantId);

    List<Subscription> findAllByPlanCode(PlanCode planCode);

    List<Subscription> findAllByStatus(SubscriptionStatus status);

    List<Subscription> findAllByStatusAndTrialEndsAtBefore(
            SubscriptionStatus status,
            Instant instant
    );
}