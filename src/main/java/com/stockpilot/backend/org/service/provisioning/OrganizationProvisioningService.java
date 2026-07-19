package com.stockpilot.backend.org.service.provisioning;

import com.stockpilot.backend.identity.api.request.RegisterOrganizationRequest;
import com.stockpilot.backend.org.entity.BusinessConfig;
import com.stockpilot.backend.org.entity.Organization;
import com.stockpilot.backend.org.repository.BusinessConfigRepository;
import com.stockpilot.backend.org.repository.OrganizationRepository;
import com.stockpilot.backend.org.service.SubscriptionService;
import com.stockpilot.backend.tenant.domain.entity.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganizationProvisioningService {

    private final OrganizationRepository organizationRepository;
    private final BusinessConfigRepository businessConfigRepository;
    private final TaxProvisioningService taxProvisioningService;
    private final SubscriptionService subscriptionService;

    @Transactional
    public void provisionDefaults(
            Tenant tenant,
            RegisterOrganizationRequest request
    ) {

        Organization organization = organizationRepository.save(
                Organization.builder()
                        .tenantId(tenant.getId())
                        .legalName(request.getOrganizationName().trim())
                        .displayName(request.getOrganizationName().trim())
                        .email(request.getEmail().trim().toLowerCase())
                        .countryCode("IN")
                        .build()
        );

        BusinessConfig businessConfig = BusinessConfig.builder()
                .tenantId(tenant.getId())
                .organization(organization)
                .build();

        businessConfigRepository.save(businessConfig);
        taxProvisioningService.provisionDefaults(tenant);
        subscriptionService.createDefaultSubscription(tenant.getId());
        // Future:
        // orgSettingsRepository.save(...)
    }
}