package com.stockpilot.backend.org.provider;

import com.stockpilot.backend.org.entity.Organization;
import com.stockpilot.backend.org.repository.OrganizationRepository;
import com.stockpilot.backend.org.exception.OrganizationNotFoundException;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrganizationProvider {

    private final OrganizationRepository organizationRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public Organization getCurrentOrganization() {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        return organizationRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new OrganizationNotFoundException(tenantId));
    }
}