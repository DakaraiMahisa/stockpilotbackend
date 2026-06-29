package com.stockpilot.backend.org.service;

import com.stockpilot.backend.identity.api.request.RegisterOrganizationRequest;
import com.stockpilot.backend.org.entity.Organization;
import com.stockpilot.backend.org.repository.OrganizationRepository;
import com.stockpilot.backend.tenant.domain.entity.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganizationProvisioningService {

    private final OrganizationRepository organizationRepository;

    @Transactional
    public void provisionDefaults(Tenant tenant, RegisterOrganizationRequest request) {

        Organization organization = Organization.builder()
                .tenantId(tenant.getId())
                .legalName(request.getOrganizationName().trim())
                .displayName(request.getOrganizationName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .countryCode("IN")
                .build();

        organizationRepository.save(organization);

        // Future:
        // businessConfigRepository.save(...)
        // orgSettingsRepository.save(...)
    }
}