package com.stockpilot.backend.shared.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class TenantContext {
    private static final ThreadLocal<UUID> currentTenant = new InheritableThreadLocal<>();

    public static void setTenantId(UUID tenantId) {
        log.trace("Setting TenantContext: {}", tenantId);
        currentTenant.set(tenantId);
    }

    public static UUID getTenantId() {
        return currentTenant.get();
    }

    public static void clear() {
        log.trace("Clearing TenantContext");
        currentTenant.remove();
    }

    public static boolean isTenantSet() {
        return currentTenant.get() != null;
    }
}

