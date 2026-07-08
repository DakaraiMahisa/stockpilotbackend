package com.stockpilot.backend.org.service.impl;

import com.stockpilot.backend.org.dto.request.LogoConfirmRequest;
import com.stockpilot.backend.org.dto.request.LogoPresignedRequest;
import com.stockpilot.backend.org.dto.response.OrganizationDto;
import com.stockpilot.backend.org.dto.request.OrganizationUpdateRequest;
import com.stockpilot.backend.org.dto.response.PresignedUploadResponse;
import com.stockpilot.backend.org.dto.storage.StoredObject;
import com.stockpilot.backend.org.entity.Organization;
import com.stockpilot.backend.org.event.OrganizationProfileUpdatedEvent;
import com.stockpilot.backend.org.mapper.OrganizationMapper;
import com.stockpilot.backend.org.provider.OrganizationProvider;
import com.stockpilot.backend.org.repository.OrganizationRepository;
import com.stockpilot.backend.org.service.OrganizationService;
import com.stockpilot.backend.shared.api.ApiMessages;
import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;
import com.stockpilot.backend.org.exception.StorageObjectNotFoundException;
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
    private final OrganizationMapper organizationMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final StorageService storageService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrganizationProvider organizationProvider;

    @Override
    @Transactional(readOnly = true)
    public OrganizationDto getProfile() {
        return organizationMapper.toDto(organizationProvider.getCurrentOrganization());
    }

    @Override
    @Transactional(readOnly = true)
    public StoredObject getOrganizationLogo() {

        Organization organization = organizationProvider.getCurrentOrganization();

        String objectKey = organization.getLogoUrl();

        if (objectKey == null || objectKey.isBlank()) {
            throw new ResourceNotFoundException(ApiMessages.ORG_LOGO_NOT_FOUND);
        }

        return storageService.getObject(objectKey);
    }

    @Override
    public OrganizationDto updateProfile(OrganizationUpdateRequest request) {

        Organization organization = organizationProvider.getCurrentOrganization();

        organizationMapper.updateEntityFromRequest(request, organization);

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

        return organizationMapper.toDto(updatedOrganization);
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

        Organization organization = organizationProvider.getCurrentOrganization();

        if (!storageService.objectExists(request.objectKey())) {
            throw new StorageObjectNotFoundException(request.objectKey());
        }

        if (organization.getLogoUrl() != null
                && !organization.getLogoUrl().isBlank()) {

            storageService.deleteObject(organization.getLogoUrl());
        }

        organization.setLogoUrl(request.objectKey());

        Organization updatedOrganization = organizationRepository.save(organization);

        eventPublisher.publishEvent(
                OrganizationProfileUpdatedEvent.of(
                        updatedOrganization.getId(),
                        updatedOrganization.getTenantId(),
                        authenticatedUserProvider.getCurrentUserId()
                )
        );
        log.info(
                "Organization logo updated. Tenant={}, Organization={}",
                updatedOrganization.getTenantId(),
                updatedOrganization.getId()
        );

        return organizationMapper.toDto(updatedOrganization);
    }

}