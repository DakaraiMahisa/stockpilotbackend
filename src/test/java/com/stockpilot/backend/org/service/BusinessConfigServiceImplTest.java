package com.stockpilot.backend.org.service;

import com.stockpilot.backend.org.dto.request.BusinessConfigUpdateRequest;
import com.stockpilot.backend.org.dto.response.BusinessConfigDto;
import com.stockpilot.backend.org.entity.BusinessConfig;
import com.stockpilot.backend.org.enums.*;
import com.stockpilot.backend.org.event.BusinessConfigChangedEvent;
import com.stockpilot.backend.org.exception.BusinessConfigNotFoundException;
import com.stockpilot.backend.org.mapper.BusinessConfigMapper;
import com.stockpilot.backend.org.repository.BusinessConfigRepository;
import com.stockpilot.backend.org.service.impl.BusinessConfigServiceImpl;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessConfigServiceImplTest {

    @Mock
    private BusinessConfigRepository businessConfigRepository;

    @Mock
    private BusinessConfigMapper businessConfigMapper;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BusinessConfigServiceImpl businessConfigService;

    @Nested
    @DisplayName("getConfiguration()")
    class GetConfiguration {

        @Test
        @DisplayName("should return business configuration")
        void shouldReturnBusinessConfiguration() {

            // Arrange
            UUID tenantId = UUID.randomUUID();

            BusinessConfig businessConfig = BusinessConfig.builder()
                    .tenantId(tenantId)
                    .timezone("Africa/Harare")
                    .currencyCode("USD")
                    .build();

            BusinessConfigDto dto = new BusinessConfigDto(
                    businessConfig.getId(),
                    "Africa/Harare",
                    "USD",
                    "$",
                    CurrencyPosition.PREFIX,
                    "dd/MM/yyyy",
                    TimeFormat.H12,
                    NumberFormat.DOT_COMMA,
                    2,
                    "01-01",
                    "en-US",
                    WeightUnit.KG,
                    DimensionUnit.CM
            );

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(tenantId);

            when(businessConfigRepository.findByTenantId(tenantId))
                    .thenReturn(Optional.of(businessConfig));

            when(businessConfigMapper.toDto(businessConfig))
                    .thenReturn(dto);

            // Act
            BusinessConfigDto result =
                    businessConfigService.getConfiguration();

            // Assert
            assertThat(result).isEqualTo(dto);

            verify(authenticatedUserProvider)
                    .getCurrentTenantId();

            verify(businessConfigRepository)
                    .findByTenantId(tenantId);

            verify(businessConfigMapper)
                    .toDto(businessConfig);

            verifyNoMoreInteractions(
                    authenticatedUserProvider,
                    businessConfigRepository,
                    businessConfigMapper,
                    eventPublisher
            );
        }

        @Test
        @DisplayName("should throw when business configuration does not exist")
        void shouldThrowWhenBusinessConfigurationDoesNotExist() {

            // Arrange
            UUID tenantId = UUID.randomUUID();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(tenantId);

            when(businessConfigRepository.findByTenantId(tenantId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessConfigNotFoundException.class,
                    () -> businessConfigService.getConfiguration()
            );

            verify(authenticatedUserProvider)
                    .getCurrentTenantId();

            verify(businessConfigRepository)
                    .findByTenantId(tenantId);

            verifyNoInteractions(
                    businessConfigMapper,
                    eventPublisher
            );

            verifyNoMoreInteractions(
                    authenticatedUserProvider,
                    businessConfigRepository
            );
        }
    }

    @Nested
    @DisplayName("updateConfiguration()")
    class UpdateConfiguration {

        @Test
        @DisplayName("should update business configuration")
        void shouldUpdateBusinessConfiguration() {

            // Arrange
            UUID tenantId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            BusinessConfig businessConfig = BusinessConfig.builder()
                    .id(UUID.randomUUID())
                    .tenantId(tenantId)
                    .timezone("UTC")
                    .currencyCode("USD")
                    .build();

            BusinessConfigUpdateRequest request =
                    new BusinessConfigUpdateRequest(
                            "Africa/Harare",
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                    );

            BusinessConfigDto dto = new BusinessConfigDto(
                    businessConfig.getId(),
                    "Africa/Harare",
                    "USD",
                    "$",
                    CurrencyPosition.PREFIX,
                    "dd/MM/yyyy",
                    TimeFormat.H12,
                    NumberFormat.DOT_COMMA,
                    2,
                    "01-01",
                    "en-US",
                    WeightUnit.KG,
                    DimensionUnit.CM
            );

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(tenantId);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(userId);

            when(businessConfigRepository.findByTenantId(tenantId))
                    .thenReturn(Optional.of(businessConfig));

            when(businessConfigRepository.save(businessConfig))
                    .thenReturn(businessConfig);

            when(businessConfigMapper.toDto(businessConfig))
                    .thenReturn(dto);

            // Act
            BusinessConfigDto result =
                    businessConfigService.updateConfiguration(request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.timezone()).isEqualTo("Africa/Harare");

            verify(authenticatedUserProvider).getCurrentTenantId();
            verify(authenticatedUserProvider).getCurrentUserId();

            verify(businessConfigRepository)
                    .findByTenantId(tenantId);

            verify(businessConfigMapper)
                    .updateEntityFromRequest(request, businessConfig);

            verify(businessConfigRepository)
                    .save(businessConfig);

            verify(eventPublisher)
                    .publishEvent(any(BusinessConfigChangedEvent.class));

            verify(businessConfigMapper)
                    .toDto(businessConfig);

            verifyNoMoreInteractions(
                    authenticatedUserProvider,
                    businessConfigRepository,
                    businessConfigMapper,
                    eventPublisher
            );
        }

        @Test
        @DisplayName("should publish business configuration changed event")
        void shouldPublishBusinessConfigurationChangedEvent() {

            // Arrange
            UUID tenantId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            UUID configId = UUID.randomUUID();

            BusinessConfig businessConfig = BusinessConfig.builder()
                    .id(configId)
                    .tenantId(tenantId)
                    .build();

            BusinessConfigUpdateRequest request =
                    new BusinessConfigUpdateRequest(
                            "Africa/Harare",
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                    );

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(tenantId);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(userId);

            when(businessConfigRepository.findByTenantId(tenantId))
                    .thenReturn(Optional.of(businessConfig));

            when(businessConfigRepository.save(businessConfig))
                    .thenReturn(businessConfig);

            when(businessConfigMapper.toDto(any()))
                    .thenReturn(mock(BusinessConfigDto.class));

            // Act
            businessConfigService.updateConfiguration(request);

            // Assert
            ArgumentCaptor<BusinessConfigChangedEvent> captor =
                    ArgumentCaptor.forClass(BusinessConfigChangedEvent.class);

            verify(eventPublisher).publishEvent(captor.capture());

            BusinessConfigChangedEvent event = captor.getValue();

            assertThat(event.businessConfigId()).isEqualTo(configId);
            assertThat(event.tenantId()).isEqualTo(tenantId);
            assertThat(event.updatedBy()).isEqualTo(userId);
            assertThat(event.occurredAt()).isNotNull();
        }

        @Test
        @DisplayName("should throw when updating missing business configuration")
        void shouldThrowWhenUpdatingMissingBusinessConfiguration() {

            // Arrange
            UUID tenantId = UUID.randomUUID();

            BusinessConfigUpdateRequest request =
                    new BusinessConfigUpdateRequest(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                    );

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(tenantId);

            when(businessConfigRepository.findByTenantId(tenantId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                    BusinessConfigNotFoundException.class,
                    () -> businessConfigService.updateConfiguration(request)
            );

            verify(authenticatedUserProvider)
                    .getCurrentTenantId();

            verify(businessConfigRepository)
                    .findByTenantId(tenantId);

            verifyNoInteractions(
                    businessConfigMapper,
                    eventPublisher
            );

            verify(authenticatedUserProvider, never())
                    .getCurrentUserId();

            verifyNoMoreInteractions(
                    authenticatedUserProvider,
                    businessConfigRepository
            );
        }
    }
}
