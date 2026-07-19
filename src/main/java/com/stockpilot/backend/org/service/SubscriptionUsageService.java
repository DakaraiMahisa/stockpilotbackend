package com.stockpilot.backend.org.service;

import com.stockpilot.backend.org.dto.response.SubscriptionUsageDto;

import java.util.UUID;

public interface SubscriptionUsageService {
    SubscriptionUsageDto getUsage(UUID tenantId);
}
