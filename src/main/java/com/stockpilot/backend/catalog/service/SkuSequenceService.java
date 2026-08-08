package com.stockpilot.backend.catalog.service;

import java.util.UUID;

public interface SkuSequenceService {
    long nextValue(UUID tenantId);
}
