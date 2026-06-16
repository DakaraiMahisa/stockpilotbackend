package com.stockpilot.backend.shared.utils;

import com.stockpilot.backend.identity.domain.model.CurrentUserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUserContext {

    public CurrentUserPrincipal getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal()
                instanceof CurrentUserPrincipal principal)) {

            throw new AccessDeniedException(
                    "No authenticated user found"
            );
        }

        return principal;
    }

    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public UUID getCurrentTenantId() {
        return getCurrentUser().getTenantId();
    }
}