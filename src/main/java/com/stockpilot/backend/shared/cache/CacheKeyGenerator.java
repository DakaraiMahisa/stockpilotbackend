package com.stockpilot.backend.shared.cache;

import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("cacheKeys")
@RequiredArgsConstructor
public class CacheKeyGenerator {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    public String tenant() {
        return authenticatedUserProvider.getCurrentTenantId().toString();
    }
}