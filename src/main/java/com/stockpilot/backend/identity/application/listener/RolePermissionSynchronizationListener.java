package com.stockpilot.backend.identity.application.listener;

import com.stockpilot.backend.identity.infrastructure.security.permission.RolePermissionSynchronizer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RolePermissionSynchronizationListener {

    private final RolePermissionSynchronizer synchronizer;

    @EventListener(ApplicationReadyEvent.class)
    public void synchronizePermissions() {
        synchronizer.synchronizeDefaultRoles();
    }
}