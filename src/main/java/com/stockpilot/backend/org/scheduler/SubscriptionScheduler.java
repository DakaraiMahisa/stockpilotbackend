package com.stockpilot.backend.org.scheduler;

import com.stockpilot.backend.org.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionScheduler {

    private final SubscriptionService subscriptionService;

    /**
     * Runs every day at midnight.
     * Expires trial subscriptions that have reached their end date.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void expireTrialSubscriptions() {

        log.info("Starting scheduled trial subscription expiration.");

        long expired = subscriptionService.expireTrialSubscriptions();

        log.info("Completed scheduled trial subscription expiration. ",expired);
    }
}