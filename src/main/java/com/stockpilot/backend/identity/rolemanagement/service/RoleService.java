package com.stockpilot.backend.identity.rolemanagement.service;

import com.stockpilot.backend.identity.domain.repository.RoleRepository;
import com.stockpilot.backend.identity.rolemanagement.dto.RoleSummaryDto;
import com.stockpilot.backend.shared.utils.CurrentUserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final CurrentUserContext currentUserContext;

    @Transactional(readOnly = true)
    public List<RoleSummaryDto> listRoles() {

        UUID tenantId = currentUserContext.getCurrentTenantId();

        return roleRepository.findByTenantId(tenantId)
                .stream()
                .map(role -> new RoleSummaryDto(
                        role.getId(),
                        role.getName().name()
                ))
                .toList();
    }
}
