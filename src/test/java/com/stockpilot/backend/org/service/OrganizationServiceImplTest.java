package com.stockpilot.backend.org.service;

import com.stockpilot.backend.common.builders.*;
import com.stockpilot.backend.org.dto.request.LogoConfirmRequest;
import com.stockpilot.backend.org.dto.request.LogoPresignedRequest;
import com.stockpilot.backend.org.dto.response.OrganizationDto;
import com.stockpilot.backend.org.dto.request.OrganizationUpdateRequest;
import com.stockpilot.backend.org.dto.response.PresignedUploadResponse;
import com.stockpilot.backend.org.entity.Organization;
import com.stockpilot.backend.org.event.OrganizationProfileUpdatedEvent;
import com.stockpilot.backend.org.mapper.OrganizationMapper;
import com.stockpilot.backend.org.repository.OrganizationRepository;
import com.stockpilot.backend.org.service.impl.OrganizationServiceImpl;
import com.stockpilot.backend.org.exception.OrganizationNotFoundException;
import com.stockpilot.backend.shared.exception.base.StorageException;
import com.stockpilot.backend.org.exception.StorageObjectNotFoundException;
import com.stockpilot.backend.shared.storage.StorageService;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceImplTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationMapper organizationMapper;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private StorageService storageService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    @Nested
    @DisplayName("GetProfileTests")
    class GetProfileTests {

        @Test
        @DisplayName("Should return organization profile")
        void shouldReturnOrganizationProfile() {

            UUID tenantId = UUID.randomUUID();

            Organization organization = OrganizationBuilder
                    .anOrganization()
                    .tenantId(tenantId)
                    .build();

            OrganizationDto organizationDto = OrganizationDtoBuilder
                    .anOrganizationDto()
                    .id(organization.getId())
                    .legalName(organization.getLegalName())
                    .displayName(organization.getDisplayName())
                    .email(organization.getEmail())
                    .phone(organization.getPhone())
                    .addressLine1(organization.getAddressLine1())
                    .addressLine2(organization.getAddressLine2())
                    .city(organization.getCity())
                    .stateProvince(organization.getStateProvince())
                    .postalCode(organization.getPostalCode())
                    .countryCode(organization.getCountryCode())
                    .gstinVatNumber(organization.getGstinVatNumber())
                    .logoUrl(organization.getLogoUrl())
                    .website(organization.getWebsite())
                    .build();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(tenantId);

            when(organizationRepository.findByTenantId(tenantId))
                    .thenReturn(Optional.of(organization));

            when(organizationMapper.toDto(organization))
                    .thenReturn(organizationDto);


            OrganizationDto result = organizationService.getProfile();


            assertThat(result)
                    .isEqualTo(organizationDto);

            verify(authenticatedUserProvider)
                    .getCurrentTenantId();

            verify(organizationRepository)
                    .findByTenantId(tenantId);

            verify(organizationMapper)
                    .toDto(organization);

            verifyNoMoreInteractions(
                    authenticatedUserProvider,
                    organizationRepository,
                    organizationMapper,
                    storageService,
                    eventPublisher
            );
        }

        @Test
        @DisplayName("Should throw when organization does not exist")
        void shouldThrowWhenOrganizationDoesNotExist() {

            UUID tenantId = UUID.randomUUID();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(tenantId);

            when(organizationRepository.findByTenantId(tenantId))
                    .thenReturn(Optional.empty());


            assertThatThrownBy(() -> organizationService.getProfile())
                    .isInstanceOf(OrganizationNotFoundException.class)
                    .hasMessageContaining(tenantId.toString());


            verify(authenticatedUserProvider)
                    .getCurrentTenantId();

            verify(organizationRepository)
                    .findByTenantId(tenantId);

            verify(organizationMapper, never())
                    .toDto(any());

            verifyNoInteractions(
                    storageService,
                    eventPublisher
            );

            verifyNoMoreInteractions(
                    authenticatedUserProvider,
                    organizationRepository,
                    organizationMapper
            );
        }
    }

    @Nested
    @DisplayName("UpdateProfileTests")
    class UpdateProfileTests {

        @Test
        @DisplayName("Should update organization profile")
        void shouldUpdateOrganizationProfile() {

            UUID tenantId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            OrganizationUpdateRequest request =
                    OrganizationUpdateRequestBuilder
                            .withDefaults()
                            .displayName("Dakarai Software Solutions")
                            .website("https://dakarai.dev")
                            .build();

            Organization organization =
                    OrganizationBuilder.anOrganization()
                            .tenantId(tenantId)
                            .build();

            OrganizationDto organizationDto =
                    OrganizationDtoBuilder.anOrganizationDto()
                            .id(organization.getId())
                            .displayName("Dakarai Software Solutions")
                            .website("https://dakarai.dev")
                            .build();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(tenantId);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(userId);

            when(organizationRepository.findByTenantId(tenantId))
                    .thenReturn(Optional.of(organization));

            when(organizationRepository.save(organization))
                    .thenReturn(organization);

            when(organizationMapper.toDto(organization))
                    .thenReturn(organizationDto);


            OrganizationDto result =
                    organizationService.updateProfile(request);


            assertThat(result)
                    .isEqualTo(organizationDto);

            verify(authenticatedUserProvider)
                    .getCurrentTenantId();

            verify(organizationRepository)
                    .findByTenantId(tenantId);

            verify(organizationMapper)
                    .updateEntityFromRequest(request, organization);

            verify(organizationRepository)
                    .save(organization);

            ArgumentCaptor<OrganizationProfileUpdatedEvent> eventCaptor =
                    ArgumentCaptor.forClass(
                            OrganizationProfileUpdatedEvent.class
                    );

            verify(eventPublisher)
                    .publishEvent(eventCaptor.capture());

            OrganizationProfileUpdatedEvent event =
                    eventCaptor.getValue();

            assertThat(event.organizationId())
                    .isEqualTo(organization.getId());

            assertThat(event.tenantId())
                    .isEqualTo(tenantId);

            assertThat(event.updatedBy())
                    .isEqualTo(userId);

            verify(organizationMapper)
                    .toDto(organization);

            InOrder inOrder = inOrder(
                    authenticatedUserProvider,
                    organizationRepository,
                    organizationMapper,
                    eventPublisher
            );

            inOrder.verify(authenticatedUserProvider)
                    .getCurrentTenantId();

            inOrder.verify(organizationRepository)
                    .findByTenantId(tenantId);

            inOrder.verify(organizationMapper)
                    .updateEntityFromRequest(request, organization);

            inOrder.verify(organizationRepository)
                    .save(organization);

            inOrder.verify(eventPublisher)
                    .publishEvent(any(OrganizationProfileUpdatedEvent.class));

            inOrder.verify(organizationMapper)
                    .toDto(organization);

            verifyNoMoreInteractions(
                    authenticatedUserProvider,
                    organizationRepository,
                    organizationMapper,
                    eventPublisher,
                    storageService
            );
        }

        @Test
        @DisplayName("Should throw when organization does not exist")
        void shouldThrowWhenOrganizationDoesNotExist() {

            UUID tenantId = UUID.randomUUID();

            OrganizationUpdateRequest request =
                    OrganizationUpdateRequestBuilder
                            .withDefaults()
                            .build();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(tenantId);

            when(organizationRepository.findByTenantId(tenantId))
                    .thenReturn(Optional.empty());


            assertThatThrownBy(() ->
                    organizationService.updateProfile(request))
                    .isInstanceOf(OrganizationNotFoundException.class)
                    .hasMessageContaining(tenantId.toString());


            verify(authenticatedUserProvider)
                    .getCurrentTenantId();

            verify(organizationRepository)
                    .findByTenantId(tenantId);

            verifyNoInteractions(
                    organizationMapper,
                    storageService,
                    eventPublisher
            );

            verifyNoMoreInteractions(
                    authenticatedUserProvider,
                    organizationRepository
            );
        }
    }

    @Nested
    @DisplayName("GeneratePresignedUrlTests")
    class GeneratePresignedUrlTests {

        @Test
        @DisplayName("Should generate presigned upload URL")
        void shouldGeneratePresignedUploadUrl() {

            UUID tenantId = UUID.randomUUID();

            LogoPresignedRequest request =
                    LogoPresignedRequestBuilder.withDefaults()
                            .build();

            PresignedUploadResponse response =
                    PresignedUploadResponseBuilder.withDefaults()
                            .build();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(tenantId);

            when(storageService.generateOrganizationLogoUploadUrl(
                    tenantId,
                    request.filename(),
                    request.contentType()
            )).thenReturn(response);


            PresignedUploadResponse result =
                    organizationService.generatePresignedUrl(request);


            assertThat(result)
                    .isEqualTo(response);

            verify(authenticatedUserProvider)
                    .getCurrentTenantId();

            verify(storageService)
                    .generateOrganizationLogoUploadUrl(
                            tenantId,
                            request.filename(),
                            request.contentType()
                    );

            verifyNoMoreInteractions(
                    authenticatedUserProvider,
                    storageService,
                    organizationRepository,
                    organizationMapper,
                    eventPublisher
            );
        }

        @Test
        @DisplayName("Should propagate storage exception when generating presigned upload URL")
        void shouldPropagateStorageExceptionWhenGeneratingPresignedUploadUrl() {

            UUID tenantId = UUID.randomUUID();

            LogoPresignedRequest request =
                    LogoPresignedRequestBuilder.withDefaults()
                            .build();

            StorageException exception =
                    new StorageException("Storage unavailable");

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(tenantId);

            when(storageService.generateOrganizationLogoUploadUrl(
                    tenantId,
                    request.filename(),
                    request.contentType()
            )).thenThrow(exception);


            assertThatThrownBy(() ->
                    organizationService.generatePresignedUrl(request))
                    .isSameAs(exception);


            verify(authenticatedUserProvider)
                    .getCurrentTenantId();

            verify(storageService)
                    .generateOrganizationLogoUploadUrl(
                            tenantId,
                            request.filename(),
                            request.contentType()
                    );

            verifyNoMoreInteractions(
                    authenticatedUserProvider,
                    storageService,
                    organizationRepository,
                    organizationMapper,
                    eventPublisher
            );
        }
    }

    @Nested
    @DisplayName("ConfirmLogoUploadTests")
    class ConfirmLogoUploadTests {

        @Test
        @DisplayName("Should confirm logo upload when organization has no existing logo")
        void shouldConfirmLogoUploadWhenOrganizationHasNoExistingLogo() {

            UUID tenantId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            Organization organization = OrganizationBuilder.anOrganization()
                    .tenantId(tenantId)
                    .logoUrl(null)
                    .build();

            LogoConfirmRequest request =
                    LogoConfirmRequestBuilder.withDefaults()
                            .build();

            OrganizationDto response =
                    OrganizationDtoBuilder.anOrganizationDto()
                            .logoUrl(request.objectKey())
                            .build();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(tenantId);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(userId);

            when(organizationRepository.findByTenantId(tenantId))
                    .thenReturn(Optional.of(organization));

            when(storageService.objectExists(request.objectKey()))
                    .thenReturn(true);

            when(organizationRepository.save(organization))
                    .thenReturn(organization);

            when(organizationMapper.toDto(organization))
                    .thenReturn(response);


            OrganizationDto result =
                    organizationService.confirmLogoUpload(request);


            assertThat(result)
                    .isEqualTo(response);

            assertThat(organization.getLogoUrl())
                    .isEqualTo(request.objectKey());

            verify(storageService, never())
                    .deleteObject(anyString());

            verify(organizationRepository)
                    .save(organization);

            verify(organizationMapper)
                    .toDto(organization);

            ArgumentCaptor<OrganizationProfileUpdatedEvent> eventCaptor =
                    ArgumentCaptor.forClass(OrganizationProfileUpdatedEvent.class);

            verify(eventPublisher)
                    .publishEvent(eventCaptor.capture());

            OrganizationProfileUpdatedEvent event = eventCaptor.getValue();

            assertThat(event.organizationId())
                    .isEqualTo(organization.getId());

            assertThat(event.tenantId())
                    .isEqualTo(tenantId);

            assertThat(event.updatedBy())
                    .isEqualTo(userId);

            assertThat(event.occurredAt())
                    .isNotNull();

            verifyNoMoreInteractions(
                    organizationRepository,
                    authenticatedUserProvider,
                    storageService,
                    organizationMapper,
                    eventPublisher
            );
        }

        @Test
        @DisplayName("Should replace existing logo")
        void shouldReplaceExistingLogo() {

            UUID tenantId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            Organization organization = OrganizationBuilder.anOrganization()
                    .tenantId(tenantId)
                    .logoUrl("org-logos/old-logo.png")
                    .build();

            LogoConfirmRequest request =
                    LogoConfirmRequestBuilder.withDefaults()
                            .objectKey("org-logos/new-logo.png")
                            .build();

            OrganizationDto response =
                    OrganizationDtoBuilder.anOrganizationDto()
                            .logoUrl(request.objectKey())
                            .build();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(tenantId);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(userId);

            when(organizationRepository.findByTenantId(tenantId))
                    .thenReturn(Optional.of(organization));

            when(storageService.objectExists(request.objectKey()))
                    .thenReturn(true);

            when(organizationRepository.save(organization))
                    .thenReturn(organization);

            when(organizationMapper.toDto(organization))
                    .thenReturn(response);


            OrganizationDto result =
                    organizationService.confirmLogoUpload(request);


            assertThat(result)
                    .isEqualTo(response);

            assertThat(organization.getLogoUrl())
                    .isEqualTo(request.objectKey());

            verify(storageService)
                    .deleteObject("org-logos/old-logo.png");

            verify(organizationRepository)
                    .save(organization);

            verify(organizationMapper)
                    .toDto(organization);

            ArgumentCaptor<OrganizationProfileUpdatedEvent> eventCaptor =
                    ArgumentCaptor.forClass(OrganizationProfileUpdatedEvent.class);

            verify(eventPublisher)
                    .publishEvent(eventCaptor.capture());

            OrganizationProfileUpdatedEvent event = eventCaptor.getValue();

            assertThat(event.organizationId())
                    .isEqualTo(organization.getId());

            assertThat(event.tenantId())
                    .isEqualTo(tenantId);

            assertThat(event.updatedBy())
                    .isEqualTo(userId);

            assertThat(event.occurredAt())
                    .isNotNull();

            verifyNoMoreInteractions(
                    organizationRepository,
                    authenticatedUserProvider,
                    storageService,
                    organizationMapper,
                    eventPublisher
            );
        }

        @Test
        @DisplayName("Should throw when uploaded logo does not exist")
        void shouldThrowWhenUploadedLogoDoesNotExist() {

            UUID tenantId = UUID.randomUUID();

            Organization organization = OrganizationBuilder.anOrganization()
                    .tenantId(tenantId)
                    .build();

            LogoConfirmRequest request =
                    LogoConfirmRequestBuilder.withDefaults()
                            .build();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(tenantId);

            when(organizationRepository.findByTenantId(tenantId))
                    .thenReturn(Optional.of(organization));

            when(storageService.objectExists(request.objectKey()))
                    .thenReturn(false);


            assertThatThrownBy(() ->
                    organizationService.confirmLogoUpload(request))
                    .isInstanceOf(StorageObjectNotFoundException.class)
                    .hasMessageContaining(request.objectKey());


            verify(storageService, never())
                    .deleteObject(anyString());

            verify(organizationRepository, never())
                    .save(any());

            verify(organizationMapper, never())
                    .toDto(any());

            verifyNoMoreInteractions(
                    organizationRepository,
                    authenticatedUserProvider,
                    storageService,
                    organizationMapper,
                    eventPublisher
            );
        }
    }

    @Test
    @DisplayName("Should throw when uploaded logo object does not exist")
    void shouldThrowWhenObjectDoesNotExist() {

        UUID tenantId = UUID.randomUUID();

        Organization organization = OrganizationBuilder.anOrganization()
                .tenantId(tenantId)
                .build();

        LogoConfirmRequest request =
                LogoConfirmRequestBuilder.withDefaults()
                        .build();

        when(authenticatedUserProvider.getCurrentTenantId())
                .thenReturn(tenantId);

        when(organizationRepository.findByTenantId(tenantId))
                .thenReturn(Optional.of(organization));

        when(storageService.objectExists(request.objectKey()))
                .thenReturn(false);

        assertThatThrownBy(() ->
                organizationService.confirmLogoUpload(request))
                .isInstanceOf(StorageObjectNotFoundException.class);

        verify(storageService, never())
                .deleteObject(anyString());

        verify(organizationRepository, never())
                .save(any());

        verify(eventPublisher, never())
                .publishEvent(any());

        verifyNoMoreInteractions(
                organizationRepository,
                authenticatedUserProvider,
                storageService,
                organizationMapper,
                eventPublisher
        );
    }

}
