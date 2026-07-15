package com.stockpilot.backend.org.service;

import com.stockpilot.backend.common.builders.*;
import com.stockpilot.backend.common.utils.TestConstants;
import com.stockpilot.backend.org.dto.request.CreateTaxClassRequest;
import com.stockpilot.backend.org.dto.request.CreateTaxRateRequest;
import com.stockpilot.backend.org.dto.request.UpdateTaxClassRequest;
import com.stockpilot.backend.org.dto.response.TaxBreakdownDto;
import com.stockpilot.backend.org.dto.response.TaxClassDto;
import com.stockpilot.backend.org.dto.response.TaxRateDto;
import com.stockpilot.backend.org.entity.TaxClass;
import com.stockpilot.backend.org.entity.TaxRate;
import com.stockpilot.backend.org.enums.RateType;
import com.stockpilot.backend.org.enums.TaxType;
import com.stockpilot.backend.org.event.TaxConfigChangedEvent;
import com.stockpilot.backend.org.exception.DuplicateTaxClassNameException;
import com.stockpilot.backend.org.exception.InvalidTaxRateException;
import com.stockpilot.backend.org.exception.TaxClassNotFoundException;
import com.stockpilot.backend.org.mapper.TaxMapper;
import com.stockpilot.backend.org.repository.TaxClassRepository;
import com.stockpilot.backend.org.repository.TaxRateRepository;
import com.stockpilot.backend.org.service.calculator.TaxCalculationService;
import com.stockpilot.backend.org.service.impl.TaxServiceImpl;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaxServiceImpl")
class TaxServiceImplTest {

    @Mock
    private TaxClassRepository taxClassRepository;

    @Mock
    private TaxRateRepository taxRateRepository;

    @Mock
    private TaxMapper taxMapper;

    @Mock
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private TaxCalculationService taxCalculationService;

    @InjectMocks
    private TaxServiceImpl taxService;

    private final UUID TENANT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Nested
    @DisplayName("getTaxClasses()")
    class GetTaxClasses {

        @Test
        @DisplayName("shouldReturnAllTaxClasses")
        void shouldReturnAllTaxClasses() {
            when(authenticatedUserProvider.getCurrentTenantId()).thenReturn(TENANT_ID);

            // Prepare tax rates: one active, one inactive using Lombok builders (no-args ctors are protected)
            TaxRate activeRate = TaxRateBuilder.aTaxRate()
                    .withRateType(RateType.VAT)
                    .withRate(BigDecimal.valueOf(18))
                    .withEffectiveFrom(LocalDate.now().minusDays(10))
                    .withEffectiveTo(null)
                    .build();

            TaxRate oldRate = TaxRateBuilder.aTaxRate()
                    .withRateType(RateType.VAT)
                    .withRate(BigDecimal.valueOf(12))
                    .withEffectiveFrom(LocalDate.now().minusYears(2))
                    .withEffectiveTo(LocalDate.now().minusYears(1))
                    .build();

            TaxClass taxClass = TaxClassBuilder.aTaxClass()
                    .withId(UUID.randomUUID())
                    .withName("Standard")
                    .withCode("STD")
                    .withTaxType(TaxType.VAT)
                    .withRates(List.of(activeRate, oldRate))
                    .build();

            when(taxClassRepository.findByTenantIdAndDeletedFalseOrderByNameAsc(TENANT_ID))
                    .thenReturn(List.of(taxClass));

            // Mapper should be called with the original entity; return a DTO containing both rates
            when(taxMapper.toDto(any(TaxClass.class))).thenAnswer(invocation -> {
                TaxClass tc = invocation.getArgument(0);
                List<TaxRateDto> rateDtos = tc.getRates().stream()
                        .map(r -> new TaxRateDto(r.getId(), r.getRateType(), r.getRate(), r.getEffectiveFrom(), r.getEffectiveTo()))
                        .toList();

                return new TaxClassDto(tc.getId(), tc.getName(), tc.getCode(), tc.getTaxType(), tc.isDefaultTaxClass(), tc.getHsnSacCode(), tc.getDescription(), rateDtos);
            });

            List<TaxClassDto> result = taxService.getTaxClasses(false);

            assertNotNull(result);
            assertEquals(1, result.size(), "Expected one tax class returned");
            TaxClassDto dto = result.get(0);
            assertEquals(2, dto.rates().size(), "When activeOnly=false all rates should be returned");
        }

        @Test
        @DisplayName("shouldReturnOnlyActiveTaxClasses")
        void shouldReturnOnlyActiveTaxClasses() {
            when(authenticatedUserProvider.getCurrentTenantId()).thenReturn(TENANT_ID);

            TaxRate activeRate = TaxRateBuilder.aTaxRate()
                    .withRateType(RateType.VAT)
                    .withRate(BigDecimal.valueOf(18))
                    .withEffectiveFrom(LocalDate.now().minusDays(10))
                    .withEffectiveTo(null)
                    .build();

            TaxRate futureRate = TaxRateBuilder.aTaxRate()
                    .withRateType(RateType.VAT)
                    .withRate(BigDecimal.valueOf(15))
                    .withEffectiveFrom(LocalDate.now().plusDays(10))
                    .withEffectiveTo(null)
                    .build();

            TaxClass taxClass = TaxClassBuilder.aTaxClass()
                    .withId(UUID.randomUUID())
                    .withName("Reduced")
                    .withCode("RED")
                    .withTaxType(TaxType.VAT)
                    .withRates(List.of(activeRate, futureRate))
                    .build();


            when(taxClassRepository.findByTenantIdAndDeletedFalseOrderByNameAsc(TENANT_ID))
                    .thenReturn(List.of(taxClass));

            // Return DTO based on the instance passed into the mapper (service will filter rates before mapping)
            when(taxMapper.toDto(any(TaxClass.class))).thenAnswer(invocation -> {
                TaxClass tc = invocation.getArgument(0);
                List<TaxRateDto> rateDtos = tc.getRates().stream()
                        .map(r -> new TaxRateDto(r.getId(), r.getRateType(), r.getRate(), r.getEffectiveFrom(), r.getEffectiveTo()))
                        .toList();

                return new TaxClassDto(tc.getId(), tc.getName(), tc.getCode(), tc.getTaxType(), tc.isDefaultTaxClass(), tc.getHsnSacCode(), tc.getDescription(), rateDtos);
            });

            List<TaxClassDto> result = taxService.getTaxClasses(true);

            assertNotNull(result);
            assertEquals(1, result.size());
            TaxClassDto dto = result.get(0);
            assertEquals(1, dto.rates().size(), "Only currently effective rates should be returned when activeOnly=true");
            assertEquals(activeRate.getRate(), dto.rates().get(0).rate());
        }

        @Test
        @DisplayName("shouldReturnEmptyListWhenNoTaxClassesExist")
        void shouldReturnEmptyListWhenNoTaxClassesExist() {
            when(authenticatedUserProvider.getCurrentTenantId()).thenReturn(TENANT_ID);

            when(taxClassRepository.findByTenantIdAndDeletedFalseOrderByNameAsc(TENANT_ID))
                    .thenReturn(List.of());

            List<TaxClassDto> result = taxService.getTaxClasses(false);

            assertNotNull(result);
            assertTrue(result.isEmpty(), "Expected empty list when repository returns no tax classes");
        }
    }

    @Nested
    @DisplayName("createTaxClass()")
    class CreateTaxClass {

        @Test
        @DisplayName("shouldCreateTaxClass")
        void shouldCreateTaxClass() {
            when(authenticatedUserProvider.getCurrentTenantId()).thenReturn(TENANT_ID);
            UUID currentUser = UUID.fromString("22222222-2222-2222-2222-222222222222");
            when(authenticatedUserProvider.getCurrentUserId()).thenReturn(currentUser);

            var rateReq = new com.stockpilot.backend.org.dto.request.CreateTaxRateRequest(
                    RateType.VAT,
                    BigDecimal.valueOf(18),
                    LocalDate.now().plusDays(1)
            );

            var request = new com.stockpilot.backend.org.dto.request.CreateTaxClassRequest(
                    "Standard",
                    "STD",
                    TaxType.VAT,
                    false,
                    null,
                    null,
                    List.of(rateReq)
            );

            TaxClass mapped = TaxClassBuilder.aTaxClass()
                    .withName(request.name())
                    .withCode(request.code().toUpperCase())
                    .withTaxType(request.taxType())
                    .build();
            mapped.setTenantId(TENANT_ID);

            UUID savedId = UUID.randomUUID();
            TaxClass saved = TaxClassBuilder.aTaxClass()
                    .withId(savedId)
                    .withName(request.name())
                    .withCode(request.code().toUpperCase())
                    .withTaxType(request.taxType())
                    .build();
            saved.setTenantId(TENANT_ID);

            TaxRate createdRate = TaxRateBuilder.aTaxRate()
                    .withRateType(rateReq.rateType())
                    .withRate(rateReq.rate())
                    .withEffectiveFrom(rateReq.effectiveFrom())
                    .build();
            createdRate.setTenantId(TENANT_ID);
            createdRate.setTaxClass(saved);

            when(taxMapper.toEntity(request)).thenReturn(mapped);

            when(taxClassRepository.existsByTenantIdAndNameIgnoreCaseAndDeletedFalse(TENANT_ID, request.name()))
                    .thenReturn(false);

            when(taxClassRepository.existsByTenantIdAndCodeIgnoreCaseAndDeletedFalse(TENANT_ID, request.code()))
                    .thenReturn(false);

            doReturn(saved).when(taxClassRepository).save(any(TaxClass.class));

            when(taxMapper.toEntity(rateReq)).thenReturn(createdRate);

            when(taxRateRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

            var dto = new TaxClassDto(saved.getId(), saved.getName(), saved.getCode(), saved.getTaxType(), saved.isDefaultTaxClass(), saved.getHsnSacCode(), saved.getDescription(), List.of());
            when(taxMapper.toDto(any(TaxClass.class))).thenReturn(dto);

            var result = taxService.createTaxClass(request);

            assertNotNull(result);
            assertEquals(dto, result);
            assertEquals(savedId, result.id());

            verify(taxMapper).toEntity(request);
            verify(taxClassRepository).save(any(TaxClass.class));
            verify(taxRateRepository).saveAll(any());
            verify(taxMapper).toDto(any(TaxClass.class));
        }

        @Test
        @DisplayName("shouldUnsetCurrentDefaultWhenCreatingDefaultTaxClass")
        void shouldUnsetCurrentDefaultWhenCreatingDefaultTaxClass() {
            when(authenticatedUserProvider.getCurrentTenantId()).thenReturn(TENANT_ID);
            when(authenticatedUserProvider.getCurrentUserId()).thenReturn(UUID.randomUUID());

            var rateReq = new com.stockpilot.backend.org.dto.request.CreateTaxRateRequest(
                    RateType.VAT,
                    BigDecimal.valueOf(18),
                    LocalDate.now().plusDays(1)
            );

            var request = new com.stockpilot.backend.org.dto.request.CreateTaxClassRequest(
                    "Standard",
                    "STD",
                    TaxType.VAT,
                    true,
                    null,
                    null,
                    List.of(rateReq)
            );

            TaxClass mapped = TaxClassBuilder.aTaxClass().withName(request.name()).withCode(request.code().toUpperCase()).build();
            mapped.setTenantId(TENANT_ID);

            TaxClass existingDefault = TaxClassBuilder.aTaxClass().withId(UUID.randomUUID()).build();
            existingDefault.setTenantId(TENANT_ID);
            existingDefault.markAsDefault();
            assertTrue(existingDefault.isDefaultTaxClass(), "Should start as default");

            when(taxMapper.toEntity(request)).thenReturn(mapped);
            when(taxClassRepository.existsByTenantIdAndNameIgnoreCaseAndDeletedFalse(TENANT_ID, request.name())).thenReturn(false);
            when(taxClassRepository.existsByTenantIdAndCodeIgnoreCaseAndDeletedFalse(TENANT_ID, request.code())).thenReturn(false);
            when(taxClassRepository.findByTenantIdAndDefaultTaxClassTrueAndDeletedFalse(TENANT_ID)).thenReturn(java.util.Optional.of(existingDefault));
            
            // For save, just return what was passed - avoids strict mode issues
            when(taxClassRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            when(taxMapper.toEntity(rateReq)).thenReturn(TaxRateBuilder.aTaxRate().withRateType(rateReq.rateType()).withRate(rateReq.rate()).withEffectiveFrom(rateReq.effectiveFrom()).build());
            when(taxRateRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
            when(taxMapper.toDto(any(TaxClass.class))).thenReturn(new TaxClassDto(mapped.getId(), request.name(), request.code().toUpperCase(), request.taxType(), true, null, null, List.of()));

            //When creating a new default tax class, service should unmark the existing default
            taxService.createTaxClass(request);

            // Verify the behavior: existing default was unmarked
            assertFalse(existingDefault.isDefaultTaxClass(), "Service should unmark existing default when creating new default");
        }

        @Test
        @DisplayName("shouldThrowWhenTaxClassNameAlreadyExists")
        void shouldThrowWhenTaxClassNameAlreadyExists() {
            when(authenticatedUserProvider.getCurrentTenantId()).thenReturn(TENANT_ID);

            var request = new com.stockpilot.backend.org.dto.request.CreateTaxClassRequest(
                    "Standard",
                    "STD",
                    TaxType.VAT,
                    false,
                    null,
                    null,
                    List.of(new com.stockpilot.backend.org.dto.request.CreateTaxRateRequest(RateType.VAT, BigDecimal.valueOf(18), LocalDate.now().plusDays(1)))
            );

            TaxClass mapped = TaxClassBuilder.aTaxClass().withName(request.name()).withCode(request.code().toUpperCase()).build();
            when(taxMapper.toEntity(request)).thenReturn(mapped);
            
            // Mock to return true, indicating name already exists
            when(taxClassRepository.existsByTenantIdAndNameIgnoreCaseAndDeletedFalse(TENANT_ID, request.name())).thenReturn(true);

            // Should throw DuplicateTaxClassNameException before checking code or saving
            var exception = assertThrows(com.stockpilot.backend.org.exception.DuplicateTaxClassNameException.class, 
                    () -> taxService.createTaxClass(request));
            assertNotNull(exception.getMessage());

            verify(taxClassRepository).existsByTenantIdAndNameIgnoreCaseAndDeletedFalse(TENANT_ID, request.name());
            verify(taxClassRepository, never()).existsByTenantIdAndCodeIgnoreCaseAndDeletedFalse(any(), any());
            verify(taxClassRepository, never()).save(any());
        }

        @Test
        @DisplayName("shouldThrowWhenTaxClassCodeAlreadyExists")
        void shouldThrowWhenTaxClassCodeAlreadyExists() {
            when(authenticatedUserProvider.getCurrentTenantId()).thenReturn(TENANT_ID);

            var request = new com.stockpilot.backend.org.dto.request.CreateTaxClassRequest(
                    "Standard",
                    "STD",
                    TaxType.VAT,
                    false,
                    null,
                    null,
                    List.of(new com.stockpilot.backend.org.dto.request.CreateTaxRateRequest(RateType.VAT, BigDecimal.valueOf(18), LocalDate.now().plusDays(1)))
            );

            TaxClass mapped = TaxClassBuilder.aTaxClass().withName(request.name()).withCode(request.code().toUpperCase()).build();
            when(taxMapper.toEntity(request)).thenReturn(mapped);
            
            // Name check passes
            when(taxClassRepository.existsByTenantIdAndNameIgnoreCaseAndDeletedFalse(TENANT_ID, request.name())).thenReturn(false);
            
            // Code check fails (already exists)
            when(taxClassRepository.existsByTenantIdAndCodeIgnoreCaseAndDeletedFalse(TENANT_ID, request.code())).thenReturn(true);

            // Should throw DuplicateTaxClassCodeException
            var exception = assertThrows(com.stockpilot.backend.org.exception.DuplicateTaxClassCodeException.class, 
                    () -> taxService.createTaxClass(request));
            assertNotNull(exception.getMessage());

            verify(taxClassRepository).existsByTenantIdAndNameIgnoreCaseAndDeletedFalse(TENANT_ID, request.name());
            verify(taxClassRepository).existsByTenantIdAndCodeIgnoreCaseAndDeletedFalse(TENANT_ID, request.code());
            verify(taxClassRepository, never()).save(any());
        }

        @Test
        @DisplayName("shouldThrowWhenRatesAreEmpty")
        void shouldThrowWhenRatesAreEmpty() {
            var request = new com.stockpilot.backend.org.dto.request.CreateTaxClassRequest(
                    "Standard",
                    "STD",
                    TaxType.VAT,
                    false,
                    null,
                    null,
                    List.of()
            );

            var validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
            var violations = validator.validate(request);

            assertFalse(violations.isEmpty(), "Expected validation violations for empty rates");
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().contains("rates")));
        }

        @Test
        @DisplayName("shouldPublishTaxConfigChangedEvent")
        void shouldPublishTaxConfigChangedEvent() {
            when(authenticatedUserProvider.getCurrentTenantId()).thenReturn(TENANT_ID);
            UUID currentUser = UUID.fromString("33333333-3333-3333-3333-333333333333");
            when(authenticatedUserProvider.getCurrentUserId()).thenReturn(currentUser);

            var rateReq = new com.stockpilot.backend.org.dto.request.CreateTaxRateRequest(
                    RateType.VAT,
                    BigDecimal.valueOf(18),
                    LocalDate.now().plusDays(1)
            );

            var request = new com.stockpilot.backend.org.dto.request.CreateTaxClassRequest(
                    "Standard",
                    "STD",
                    TaxType.VAT,
                    false,
                    null,
                    null,
                    List.of(rateReq)
            );

            TaxClass mapped = TaxClassBuilder.aTaxClass().withName(request.name()).withCode(request.code()).build();
            TaxClass saved = TaxClassBuilder.aTaxClass().withId(UUID.randomUUID()).withName(request.name()).withCode(request.code()).build();

            when(taxMapper.toEntity(request)).thenReturn(mapped);
            when(taxClassRepository.existsByTenantIdAndNameIgnoreCaseAndDeletedFalse(TENANT_ID, request.name())).thenReturn(false);
            when(taxClassRepository.existsByTenantIdAndCodeIgnoreCaseAndDeletedFalse(TENANT_ID, request.code())).thenReturn(false);
            when(taxClassRepository.save(mapped)).thenReturn(saved);

            when(taxMapper.toEntity(rateReq)).thenReturn(TaxRateBuilder.aTaxRate().withRateType(rateReq.rateType()).withRate(rateReq.rate()).withEffectiveFrom(rateReq.effectiveFrom()).build());
            when(taxRateRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

            when(taxMapper.toDto(saved)).thenReturn(new TaxClassDto(saved.getId(), saved.getName(), saved.getCode(), saved.getTaxType(), saved.isDefaultTaxClass(), saved.getHsnSacCode(), saved.getDescription(), List.of()));

            taxService.createTaxClass(request);

            ArgumentCaptor<com.stockpilot.backend.org.event.TaxConfigChangedEvent> captor = ArgumentCaptor.forClass(com.stockpilot.backend.org.event.TaxConfigChangedEvent.class);
            verify(eventPublisher).publishEvent(captor.capture());

            var event = captor.getValue();
            assertEquals(saved.getId(), event.taxClassId());
            assertEquals(TENANT_ID, event.tenantId());
            assertEquals(currentUser, event.changedBy());
            assertNotNull(event.occurredAt());
        }
    }

    @Nested
    @DisplayName("updateTaxClass()")
    class UpdateTaxClassTests {

        @Test
        @DisplayName("shouldUpdateTaxClass")
        void shouldUpdateTaxClass() {

            UpdateTaxClassRequest request =
                    TaxClassBuilders.updateTaxClassRequest();

            TaxClass taxClass = TaxClassBuilders.aTaxClass().build();

            TaxClassDto dto =
                    TaxClassBuilders.taxClassDto();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(TestConstants.USER_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.of(taxClass));

            when(taxClassRepository
                    .existsByTenantIdAndNameIgnoreCaseAndDeletedFalseAndIdNot(
                            TestConstants.TENANT_ID,
                            taxClass.getName(),
                            TestConstants.TAX_CLASS_ID
                    ))
                    .thenReturn(false);

            when(taxClassRepository.save(taxClass))
                    .thenReturn(taxClass);

            when(taxMapper.toDto(taxClass))
                    .thenReturn(dto);

            TaxClassDto result = taxService.updateTaxClass(
                    TestConstants.TAX_CLASS_ID,
                    request
            );

            assertThat(result).isEqualTo(dto);

            verify(taxMapper)
                    .updateEntityFromRequest(
                            request,
                            taxClass
                    );

            verify(taxClassRepository)
                    .save(taxClass);

            verify(eventPublisher)
                    .publishEvent(any(TaxConfigChangedEvent.class));
        }


        @Test
        @DisplayName("shouldIgnoreNullFields")
        void shouldIgnoreNullFields() {

            UpdateTaxClassRequest request = new UpdateTaxClassRequest(
                    null,
                    null,
                    null
            );

            TaxClass taxClass = TaxClassBuilders.aTaxClass().build();

            TaxClassDto dto = TaxClassBuilders.taxClassDto();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(TestConstants.USER_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.of(taxClass));

            when(taxClassRepository
                    .existsByTenantIdAndNameIgnoreCaseAndDeletedFalseAndIdNot(
                            TestConstants.TENANT_ID,
                            taxClass.getName(),
                            TestConstants.TAX_CLASS_ID
                    ))
                    .thenReturn(false);

            when(taxClassRepository.save(taxClass))
                    .thenReturn(taxClass);

            when(taxMapper.toDto(taxClass))
                    .thenReturn(dto);

            taxService.updateTaxClass(
                    TestConstants.TAX_CLASS_ID,
                    request
            );

            verify(taxMapper)
                    .updateEntityFromRequest(
                            request,
                            taxClass
                    );

            verify(taxClassRepository)
                    .save(taxClass);

            verify(eventPublisher)
                    .publishEvent(any(TaxConfigChangedEvent.class));
        }

        @Test
        @DisplayName("shouldThrowWhenTaxClassNotFound")
        void shouldThrowWhenTaxClassNotFound() {

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    taxService.updateTaxClass(
                            TestConstants.TAX_CLASS_ID,
                            TaxClassBuilders.updateTaxClassRequest()
                    ))
                    .isInstanceOf(TaxClassNotFoundException.class);

            verify(taxClassRepository, never())
                    .save(any());

            verify(eventPublisher, never())
                    .publishEvent(any());
        }

        @Test
        @DisplayName("shouldThrowWhenNameAlreadyExists")
        void shouldThrowWhenNameAlreadyExists() {

            TaxClass taxClass = TaxClassBuilders.aTaxClass().build();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(TestConstants.USER_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.of(taxClass));

            when(taxClassRepository
                    .existsByTenantIdAndNameIgnoreCaseAndDeletedFalseAndIdNot(
                            TestConstants.TENANT_ID,
                            taxClass.getName(),
                            TestConstants.TAX_CLASS_ID
                    ))
                    .thenReturn(true);

            assertThatThrownBy(() ->
                    taxService.updateTaxClass(
                            TestConstants.TAX_CLASS_ID,
                            TaxClassBuilders.updateTaxClassRequest()
                    ))
                    .isInstanceOf(DuplicateTaxClassNameException.class);

            verify(taxClassRepository, never())
                    .save(any());

            verify(eventPublisher, never())
                    .publishEvent(any());
        }

        @Test
        @DisplayName("shouldPublishTaxConfigChangedEvent")
        void shouldPublishTaxConfigChangedEvent() {

            TaxClass taxClass = TaxClassBuilders.aTaxClass().build();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(TestConstants.USER_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.of(taxClass));

            when(taxClassRepository
                    .existsByTenantIdAndNameIgnoreCaseAndDeletedFalseAndIdNot(
                            TestConstants.TENANT_ID,
                            taxClass.getName(),
                            TestConstants.TAX_CLASS_ID
                    ))
                    .thenReturn(false);

            when(taxClassRepository.save(taxClass))
                    .thenReturn(taxClass);

            when(taxMapper.toDto(taxClass))
                    .thenReturn(TaxClassBuilders.taxClassDto());

            taxService.updateTaxClass(
                    TestConstants.TAX_CLASS_ID,
                    TaxClassBuilders.updateTaxClassRequest()
            );

            ArgumentCaptor<TaxConfigChangedEvent> eventCaptor =
                    ArgumentCaptor.forClass(TaxConfigChangedEvent.class);

            verify(eventPublisher).publishEvent(eventCaptor.capture());

            TaxConfigChangedEvent event = eventCaptor.getValue();

            assertThat(event.taxClassId()).isEqualTo(TestConstants.TAX_CLASS_ID);
            assertThat(event.tenantId()).isEqualTo(TestConstants.TENANT_ID);

        }

    }

    @Nested
    @DisplayName("addTaxRate()")
    class AddTaxRateTests{

        @Test
        @DisplayName("shouldAddFutureTaxRate")
        void shouldAddFutureTaxRate() {

            CreateTaxRateRequest request =
                    TaxRateBuilders.createCgstRateRequest();

            TaxClass taxClass = TaxClassBuilders.aTaxClass().build();

            TaxRate newRate = TaxRateBuilders.aTaxRate().build();

            TaxRateDto dto = TaxRateBuilders.taxRateDto();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(TestConstants.USER_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.of(taxClass));

            when(taxRateRepository.findCurrentRate(
                    TestConstants.TAX_CLASS_ID,
                    request.rateType()
            )).thenReturn(Optional.empty());

            when(taxMapper.toEntity(request))
                    .thenReturn(newRate);

            when(taxRateRepository.save(newRate))
                    .thenReturn(newRate);

            when(taxMapper.toDto(newRate))
                    .thenReturn(dto);

            TaxRateDto result = taxService.addTaxRate(
                    TestConstants.TAX_CLASS_ID,
                    request
            );

            assertThat(result).isEqualTo(dto);

            verify(taxMapper).toEntity(request);
            verify(taxRateRepository).save(newRate);
        }

        @Test
        @DisplayName("shouldClosePreviousRate")
        void shouldClosePreviousRate() {

            CreateTaxRateRequest request =
                    TaxRateBuilders.createCgstRateRequest();

            TaxClass taxClass = TaxClassBuilders.aTaxClass().build();

            TaxRate previousRate = TaxRateBuilders.aTaxRate()
                    .effectiveFrom(LocalDate.now().minusMonths(1))
                    .effectiveTo(null)
                    .build();

            TaxRate newRate = TaxRateBuilders.aTaxRate().build();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(TestConstants.USER_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.of(taxClass));

            when(taxRateRepository.findCurrentRate(
                    TestConstants.TAX_CLASS_ID,
                    request.rateType()
            )).thenReturn(Optional.of(previousRate));

            when(taxMapper.toEntity(request))
                    .thenReturn(newRate);

            when(taxRateRepository.save(previousRate))
                    .thenReturn(previousRate);

            when(taxRateRepository.save(newRate))
                    .thenReturn(newRate);

            when(taxMapper.toDto(newRate))
                    .thenReturn(TaxRateBuilders.taxRateDto());

            taxService.addTaxRate(
                    TestConstants.TAX_CLASS_ID,
                    request
            );

            ArgumentCaptor<TaxRate> captor =
                    ArgumentCaptor.forClass(TaxRate.class);

            verify(taxRateRepository, times(2))
                    .save(captor.capture());

            List<TaxRate> savedRates = captor.getAllValues();

            TaxRate closedRate = savedRates.get(0);
            TaxRate insertedRate = savedRates.get(1);

            assertThat(closedRate.getEffectiveTo())
                    .isEqualTo(request.effectiveFrom().minusDays(1));

            assertThat(insertedRate.getEffectiveFrom())
                    .isEqualTo(request.effectiveFrom());

            assertThat(insertedRate.getTaxClass())
                    .isEqualTo(taxClass);
        }

        @Test
        @DisplayName("shouldThrowWhenEffectiveDateIsToday")
        void shouldThrowWhenEffectiveDateIsToday() {

            CreateTaxRateRequest request = new CreateTaxRateRequest(
                    RateType.CGST,
                    new BigDecimal("9.000"),
                    LocalDate.now()
            );

            TaxClass taxClass = TaxClassBuilders.aTaxClass().build();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(TestConstants.USER_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.of(taxClass));

            assertThatThrownBy(() ->
                    taxService.addTaxRate(
                            TestConstants.TAX_CLASS_ID,
                            request
                    ))
                    .isInstanceOf(InvalidTaxRateException.class)
                    .hasMessageContaining("future");

            verify(taxRateRepository, never())
                    .findCurrentRate(any(), any());
            verify(taxMapper, never())
                    .toEntity(any(CreateTaxClassRequest.class));

            verify(taxRateRepository, never())
                    .save(any());

            verify(eventPublisher, never())
                    .publishEvent(any());
        }

        @Test
        @DisplayName("shouldThrowWhenEffectiveDateIsPast")
        void shouldThrowWhenEffectiveDateIsPast() {

            CreateTaxRateRequest request = new CreateTaxRateRequest(
                    RateType.CGST,
                    new BigDecimal("9.000"),
                    LocalDate.now().minusDays(1)
            );

            TaxClass taxClass = TaxClassBuilders.aTaxClass().build();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(TestConstants.USER_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.of(taxClass));

            assertThatThrownBy(() ->
                    taxService.addTaxRate(
                            TestConstants.TAX_CLASS_ID,
                            request
                    ))
                    .isInstanceOf(InvalidTaxRateException.class)
                    .hasMessageContaining("future");

            verify(taxRateRepository, never())
                    .findCurrentRate(any(), any());

            verify(taxMapper, never())
                    .toEntity(any(CreateTaxRateRequest.class));

            verify(taxRateRepository, never())
                    .save(any());

            verify(eventPublisher, never())
                    .publishEvent(any());
        }

        @Test
        @DisplayName("shouldThrowWhenEffectiveDateIsBeforeCurrentRate")
        void shouldThrowWhenEffectiveDateIsBeforeCurrentRate() {

            CreateTaxRateRequest request = new CreateTaxRateRequest(
                    RateType.CGST,
                    new BigDecimal("9.000"),
                    LocalDate.now().plusDays(5)
            );

            TaxClass taxClass = TaxClassBuilders.aTaxClass().build();

            TaxRate currentRate = TaxRateBuilders.aTaxRate()
                    .effectiveFrom(LocalDate.now().plusDays(10))
                    .effectiveTo(null)
                    .build();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(TestConstants.USER_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.of(taxClass));

            when(taxRateRepository.findCurrentRate(
                    TestConstants.TAX_CLASS_ID,
                    RateType.CGST
            )).thenReturn(Optional.of(currentRate));

            assertThatThrownBy(() ->
                    taxService.addTaxRate(
                            TestConstants.TAX_CLASS_ID,
                            request
                    ))
                    .isInstanceOf(InvalidTaxRateException.class)
                    .hasMessageContaining("Effective date must be after");

            verify(taxMapper, never())
                    .toEntity(any(CreateTaxRateRequest.class));

            verify(taxRateRepository, never())
                    .save(any());

            verify(eventPublisher, never())
                    .publishEvent(any());
        }


        @Test
        @DisplayName("shouldThrowWhenTaxClassNotFound")
        void shouldThrowWhenTaxClassNotFound() {

            CreateTaxRateRequest request =
                    TaxRateBuilders.createCgstRateRequest();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(TestConstants.USER_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    taxService.addTaxRate(
                            TestConstants.TAX_CLASS_ID,
                            request
                    ))
                    .isInstanceOf(TaxClassNotFoundException.class);

            verify(taxRateRepository, never())
                    .findCurrentRate(any(), any());

            verify(taxMapper, never())
                    .toEntity(any(CreateTaxRateRequest.class));

            verify(taxRateRepository, never())
                    .save(any());

            verify(eventPublisher, never())
                    .publishEvent(any());
        }

        @Test
        @DisplayName("shouldPublishTaxConfigChangedEvent")
        void shouldPublishTaxConfigChangedEvent() {

            CreateTaxRateRequest request =
                    TaxRateBuilders.createCgstRateRequest();

            TaxClass taxClass = TaxClassBuilders.aTaxClass().build();

            TaxRate previousRate = TaxRateBuilders.aTaxRate()
                    .effectiveFrom(request.effectiveFrom().minusMonths(1))
                    .effectiveTo(null)
                    .build();

            TaxRate newRate = TaxRateBuilders.aTaxRate()
                    .effectiveFrom(request.effectiveFrom())
                    .build();

            TaxRateDto dto = TaxRateBuilders.taxRateDto();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(authenticatedUserProvider.getCurrentUserId())
                    .thenReturn(TestConstants.USER_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.of(taxClass));

            when(taxRateRepository.findCurrentRate(
                    TestConstants.TAX_CLASS_ID,
                    request.rateType()
            )).thenReturn(Optional.of(previousRate));

            when(taxMapper.toEntity(request))
                    .thenReturn(newRate);

            when(taxRateRepository.save(any(TaxRate.class)))
                    .thenReturn(previousRate)
                    .thenReturn(newRate);

            when(taxMapper.toDto(newRate))
                    .thenReturn(dto);

            taxService.addTaxRate(
                    TestConstants.TAX_CLASS_ID,
                    request
            );

            ArgumentCaptor<TaxConfigChangedEvent> captor =
                    ArgumentCaptor.forClass(TaxConfigChangedEvent.class);

            verify(eventPublisher).publishEvent(captor.capture());

            TaxConfigChangedEvent event = captor.getValue();

            assertThat(event.taxClassId())
                    .isEqualTo(TestConstants.TAX_CLASS_ID);

            assertThat(event.tenantId())
                    .isEqualTo(TestConstants.TENANT_ID);

        }

    }

    @Nested
    @DisplayName("resolveTax()")
    class ResolveTaxTests {

        @Test
        @DisplayName("shouldResolveGstTax")
        void shouldResolveGstTax() {

            BigDecimal amount = new BigDecimal("1000.00");
            LocalDate transactionDate = LocalDate.now();

            TaxClass taxClass = TaxClassBuilders.aTaxClass()
                    .taxType(TaxType.GST)
                    .build();

            List<TaxRate> rates = List.of(
                    TaxRateBuilders.aTaxRate()
                            .rateType(RateType.CGST)
                            .rate(new BigDecimal("9.000"))
                            .build(),
                    TaxRateBuilders.aTaxRate()
                            .rateType(RateType.SGST)
                            .rate(new BigDecimal("9.000"))
                            .build()
            );

            TaxBreakdownDto expected = TaxBreakdownBuilders.gstBreakdown();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.of(taxClass));

            when(taxRateRepository.findEffectiveRates(
                    TestConstants.TAX_CLASS_ID,
                    transactionDate
            )).thenReturn(rates);

            when(taxCalculationService.calculate(
                    taxClass,
                    amount,
                    rates
            )).thenReturn(expected);

            TaxBreakdownDto result = taxService.resolveTax(
                    TestConstants.TAX_CLASS_ID,
                    amount,
                    transactionDate
            );

            assertThat(result).isEqualTo(expected);
        }


        @Test
        @DisplayName("shouldResolveVatTax")
        void shouldResolveVatTax() {

            BigDecimal amount = new BigDecimal("1000.00");
            LocalDate transactionDate = LocalDate.now();

            TaxClass taxClass = TaxClassBuilders.aTaxClass()
                    .taxType(TaxType.VAT)
                    .build();

            List<TaxRate> rates = List.of(
                    TaxRateBuilders.aTaxRate()
                            .rateType(RateType.VAT)
                            .rate(new BigDecimal("15.000"))
                            .build()
            );

            TaxBreakdownDto expected = TaxBreakdownBuilders.vatBreakdown();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.of(taxClass));

            when(taxRateRepository.findEffectiveRates(
                    TestConstants.TAX_CLASS_ID,
                    transactionDate
            )).thenReturn(rates);

            when(taxCalculationService.calculate(
                    taxClass,
                    amount,
                    rates
            )).thenReturn(expected);

            TaxBreakdownDto result = taxService.resolveTax(
                    TestConstants.TAX_CLASS_ID,
                    amount,
                    transactionDate
            );

            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("shouldResolveNoTax")
        void shouldResolveNoTax() {

            BigDecimal amount = new BigDecimal("1000.00");
            LocalDate transactionDate = LocalDate.now();

            TaxClass taxClass = TaxClassBuilders.aTaxClass()
                    .taxType(TaxType.NONE)
                    .build();

            TaxBreakdownDto expected = TaxBreakdownBuilders.noTaxBreakdown();

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.of(taxClass));

            when(taxRateRepository.findEffectiveRates(
                    TestConstants.TAX_CLASS_ID,
                    transactionDate
            )).thenReturn(List.of());

            when(taxCalculationService.calculate(
                    eq(taxClass),
                    eq(amount),
                    anyList()
            )).thenReturn(expected);

            TaxBreakdownDto result = taxService.resolveTax(
                    TestConstants.TAX_CLASS_ID,
                    amount,
                    transactionDate
            );

            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("shouldThrowWhenTaxClassNotFound")
        void shouldThrowWhenTaxClassNotFound() {

            when(authenticatedUserProvider.getCurrentTenantId())
                    .thenReturn(TestConstants.TENANT_ID);

            when(taxClassRepository.findByIdAndTenantIdAndDeletedFalse(
                    TestConstants.TAX_CLASS_ID,
                    TestConstants.TENANT_ID
            )).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    taxService.resolveTax(
                            TestConstants.TAX_CLASS_ID,
                            BigDecimal.TEN,
                            LocalDate.now()
                    ))
                    .isInstanceOf(TaxClassNotFoundException.class);

            verify(taxRateRepository, never())
                    .findEffectiveRates(any(), any());

            verifyNoInteractions(taxCalculationService);
        }

    }

}