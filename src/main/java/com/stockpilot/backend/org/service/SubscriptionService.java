package com.stockpilot.backend.org.service;

import com.stockpilot.backend.org.dto.request.SubscriptionUpgradeRequest;
import com.stockpilot.backend.org.dto.response.SubscriptionDto;
import com.stockpilot.backend.org.dto.response.UpgradeRequestResponse;
import com.stockpilot.backend.org.entity.Subscription;

import java.util.UUID;

public interface SubscriptionService {

    SubscriptionDto getCurrentSubscription();

    Subscription getSubscription(UUID tenantId);

    UpgradeRequestResponse submitUpgradeRequest(
            SubscriptionUpgradeRequest request
    );

    Subscription createDefaultSubscription(UUID tenantId);

    long expireTrialSubscriptions();;
}