package com.stockpilot.backend.org.service.impl;

import com.stockpilot.backend.org.dto.*;
import com.stockpilot.backend.org.entity.Organization;
import com.stockpilot.backend.org.event.OrganizationProfileUpdatedEvent;
import com.stockpilot.backend.org.mapper.OrgMapper;
import com.stockpilot.backend.org.repository.OrganizationRepository;
import com.stockpilot.backend.org.service.OrganizationService;
import com.stockpilot.backend.shared.exception.OrganizationNotFoundException;
import com.stockpilot.backend.shared.exception.StorageException;
import com.stockpilot.backend.shared.exception.StorageObjectNotFoundException;
import com.stockpilot.backend.shared.storage.StorageService;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrgMapper orgMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final StorageService storageService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public OrganizationDto getProfile() {
        return orgMapper.toDto(getCurrentOrganization());
    }

    private Organization getCurrentOrganization() {
        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        return organizationRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new OrganizationNotFoundException(tenantId));
    }

    @Override
    public OrganizationDto updateProfile(OrganizationUpdateRequest request) {

        Organization organization = getCurrentOrganization();

        orgMapper.updateEntityFromRequest(request, organization);

        Organization updatedOrganization = organizationRepository.save(organization);

        eventPublisher.publishEvent(
                OrganizationProfileUpdatedEvent.of(
                        updatedOrganization.getId(),
                        updatedOrganization.getTenantId(),
                        authenticatedUserProvider.getCurrentUserId()
                )
        );

        log.info(
                "Organization profile updated. Tenant={}, Organization={}, UpdatedBy={}",
                updatedOrganization.getTenantId(),
                updatedOrganization.getId(),
                authenticatedUserProvider.getCurrentUserId()
        );

        return orgMapper.toDto(updatedOrganization);
    }

    @Override
    @Transactional(readOnly = true)
    public PresignedUploadResponse generatePresignedUrl(
            LogoPresignedRequest request
    ) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        log.debug(
                "Generating organization logo upload URL for tenant {}",
                tenantId
        );

        return storageService.generateOrganizationLogoUploadUrl(
                tenantId,
                request.filename(),
                request.contentType()
        );
    }

    @Override
    public OrganizationDto confirmLogoUpload(LogoConfirmRequest request) {

        Organization organization = getCurrentOrganization();

        if (!storageService.objectExists(request.objectKey())) {
            throw new StorageObjectNotFoundException(request.objectKey());
        }

        if (organization.getLogoUrl() != null
                && !organization.getLogoUrl().isBlank()) {

            storageService.deleteObject(organization.getLogoUrl());
        }

        organization.setLogoUrl(request.objectKey());

        Organization updatedOrganization = organizationRepository.save(organization);

        log.info(
                "Organization logo updated. Tenant={}, Organization={}",
                updatedOrganization.getTenantId(),
                updatedOrganization.getId()
        );

        return orgMapper.toDto(updatedOrganization);
    }

}