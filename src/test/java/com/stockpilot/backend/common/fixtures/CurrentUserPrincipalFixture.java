package com.stockpilot.backend.common.fixtures;



import com.stockpilot.backend.identity.domain.model.CurrentUserPrincipal;

import java.util.Set;
import java.util.UUID;

public final class CurrentUserPrincipalFixture {

    private CurrentUserPrincipalFixture() {
    }

    public static CurrentUserPrincipal owner() {

        return CurrentUserPrincipal.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .tenantId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .email("owner@example.com")
                .password("password")
                .permissions(Set.of(
                        "users:read",
                        "users:create",
                        "users:update",
                        "users:invite"
                ))
                .enabled(true)
                .locked(false)
                .build();
    }

    public static CurrentUserPrincipal manager() {

        return CurrentUserPrincipal.builder()
                .id(UUID.fromString("33333333-3333-3333-3333-333333333333"))
                .tenantId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .email("manager@example.com")
                .password("password")
                .permissions(Set.of(
                        "users:read"
                ))
                .enabled(true)
                .locked(false)
                .build();
    }

    public static CurrentUserPrincipal cashier() {

        return CurrentUserPrincipal.builder()
                .id(UUID.fromString("44444444-4444-4444-4444-444444444444"))
                .tenantId(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .email("cashier@example.com")
                .password("password")
                .permissions(Set.of())
                .enabled(true)
                .locked(false)
                .build();
    }

}