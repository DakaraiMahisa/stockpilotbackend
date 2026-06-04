package com.stockpilot.backend.identity.application.service;

import com.stockpilot.backend.identity.domain.entity.Role;
import com.stockpilot.backend.identity.domain.enums.RoleName;
import com.stockpilot.backend.identity.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleProvisioningService {

    private final RoleRepository roleRepository;

    @Transactional
    public void provisionDefaultRoles(UUID tenantId) {

        for (RoleName roleName : RoleName.values()) {

            Role role = Role.builder()
                    .tenantId(tenantId)
                    .name(roleName)
                    .build();

            roleRepository.save(role);
        }
    }
}