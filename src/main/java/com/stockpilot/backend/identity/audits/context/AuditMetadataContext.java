package com.stockpilot.backend.identity.audits.context;


import java.util.HashMap;
import java.util.Map;

public final class AuditMetadataContext {

    private static final ThreadLocal<Map<String, Object>> METADATA =
            ThreadLocal.withInitial(HashMap::new);

    private AuditMetadataContext() {
    }

    public static void put(String key, Object value) {
        METADATA.get().put(key, value);
    }

    public static void putAll(Map<String, Object> values) {
        METADATA.get().putAll(values);
    }

    public static Map<String, Object> get() {
        return Map.copyOf(METADATA.get());
    }

    public static void clear() {
        METADATA.remove();
    }
}