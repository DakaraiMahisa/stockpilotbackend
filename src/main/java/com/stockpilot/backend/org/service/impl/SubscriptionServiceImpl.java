package com.stockpilot.backend.org.service.impl;

import com.stockpilot.backend.org.dto.request.SubscriptionUpgradeRequest;
import com.stockpilot.backend.org.dto.response.SubscriptionDto;
import com.stockpilot.backend.org.dto.response.SubscriptionUsageDto;
import com.stockpilot.backend.org.dto.response.UpgradeRequestResponse;
import com.stockpilot.backend.org.entity.Subscription;
import com.stockpilot.backend.org.enums.PlanCode;
import com.stockpilot.backend.org.enums.SubscriptionStatus;
import com.stockpilot.backend.org.event.UpgradeRequestedEvent;
import com.stockpilot.backend.org.mapper.SubscriptionMapper;
import com.stockpilot.backend.org.repository.SubscriptionRepository;
import com.stockpilot.backend.org.service.SubscriptionService;
import com.stockpilot.backend.org.service.SubscriptionUsageService;
import com.stockpilot.backend.shared.exception.base.DuplicateResourceException;
import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ApplicationEventPublisher eventPublisher;
    private final SubscriptionUsageService subscriptionUsageService;

    @Override
    public Subscription createDefaultSubscription(UUID tenantId) {

        Subscription subscription = Subscription.builder()
                .tenantId(tenantId)
                .planCode(PlanCode.TRIAL)
                .status(SubscriptionStatus.TRIAL)
                .planStartedAt(Instant.now())
                .trialEndsAt(Instant.now().plus(14, ChronoUnit.DAYS))
                .planExpiresAt(null)
                .maxUsers(3)
                .maxBranches(1)
                .maxSkus(100)
                .active(true)
                .build();

        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public Subscription getSubscription(UUID tenantId) {

        return subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found."
                ));
    }

    @Override
    public UpgradeRequestResponse submitUpgradeRequest(
            SubscriptionUpgradeRequest request) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Subscription subscription = getSubscription(tenantId);
        PlanCode requestedPlan = PlanCode.valueOf(request.requestedPlan().name());
        if (subscription.getPlanCode() == requestedPlan) {
            throw new DuplicateResourceException(
                    "Tenant is already subscribed to this plan."
            );
        }

        UUID requestId = UUID.randomUUID();

        eventPublisher.publishEvent(
                new UpgradeRequestedEvent(
                        requestId,
                        tenantId,
                        request.requestedPlan(),
                        request.notes()
                )
        );

        return UpgradeRequestResponse.builder()
                .requestId(requestId)
                .message("Upgrade request received — our team will contact you.")
                .build();
    }

    @Override
    public long expireTrialSubscriptions() {

        List<Subscription> subscriptions =
                subscriptionRepository.findAllByStatusAndTrialEndsAtBefore(
                        SubscriptionStatus.TRIAL,
                        Instant.now()
                );

        subscriptions.forEach(subscription -> {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscription.setActive(false);
        });
        return subscriptions.size();
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionDto getCurrentSubscription() {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Subscription subscription = getSubscription(tenantId);
        SubscriptionUsageDto usage =
                subscriptionUsageService.getUsage(tenantId);

        return SubscriptionDto.builder()
                .planCode(subscription.getPlanCode())
                .status(subscription.getStatus())
                .planStartedAt(subscription.getPlanStartedAt())
                .trialEndsAt(subscription.getTrialEndsAt())
                .planExpiresAt(subscription.getPlanExpiresAt())
                .active(subscription.isActive())
                .limits(subscriptionMapper.toLimitsDto(subscription))
                .usage(usage)
                .build();
    }


}
