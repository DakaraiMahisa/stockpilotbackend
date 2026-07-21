package com.stockpilot.backend.org.service.impl;

import com.stockpilot.backend.org.dto.request.UpdateGeneralSettingsRequest;
import com.stockpilot.backend.org.dto.request.UpdateInvitePolicyRequest;
import com.stockpilot.backend.org.dto.request.UpdatePasswordPolicyRequest;
import com.stockpilot.backend.org.dto.request.UpdateSessionPolicyRequest;
import com.stockpilot.backend.org.dto.response.OrgSettingsDto;
import com.stockpilot.backend.org.entity.OrgSettings;
import com.stockpilot.backend.org.entity.Organization;
import com.stockpilot.backend.org.mapper.OrgSettingsMapper;
import com.stockpilot.backend.org.repository.OrgSettingsRepository;
import com.stockpilot.backend.org.service.OrgSettingsService;
import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrgSettingsServiceImpl implements OrgSettingsService {

    private final OrgSettingsRepository orgSettingsRepository;
    private final OrgSettingsMapper orgSettingsMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    @Transactional(readOnly = true)
    public OrgSettingsDto getSettings() {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        return orgSettingsMapper.toDto(
                getSettings(tenantId)
        );
    }

    @Override
    public OrgSettingsDto updatePasswordPolicy(
            UpdatePasswordPolicyRequest request
    ) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        OrgSettings settings = getSettings(tenantId);

        orgSettingsMapper.updatePasswordPolicy(
                request,
                settings
        );

        return orgSettingsMapper.toDto(settings);
    }

    @Override
    public OrgSettingsDto updateSessionPolicy(
            UpdateSessionPolicyRequest request
    ) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        OrgSettings settings = getSettings(tenantId);

        orgSettingsMapper.updateSessionPolicy(
                request,
                settings
        );

        return orgSettingsMapper.toDto(settings);
    }

    @Override
    public OrgSettingsDto updateInvitePolicy(
            UpdateInvitePolicyRequest request
    ) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        OrgSettings settings = getSettings(tenantId);

        orgSettingsMapper.updateInvitePolicy(
                request,
                settings
        );

        return orgSettingsMapper.toDto(settings);
    }

    @Override
    public OrgSettingsDto updateGeneralSettings(
            UpdateGeneralSettingsRequest request
    ) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        OrgSettings settings = getSettings(tenantId);

        orgSettingsMapper.updateGeneralSettings(
                request,
                settings
        );

        return orgSettingsMapper.toDto(settings);
    }

    @Override
    public OrgSettings createDefaultSettings(
            Organization organization
    ) {

        OrgSettings settings = OrgSettings.builder()
                .tenantId(organization.getTenantId())
                .organization(organization)
                .build();

        return orgSettingsRepository.save(settings);
    }

    @Override
    @Transactional(readOnly = true)
    public OrgSettings getSettings(UUID tenantId) {

        return orgSettingsRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Organization settings not found."
                        )
                );
    }
}