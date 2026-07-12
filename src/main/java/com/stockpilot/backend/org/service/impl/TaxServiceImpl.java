package com.stockpilot.backend.org.service.impl;

import com.stockpilot.backend.org.dto.request.CreateTaxClassRequest;
import com.stockpilot.backend.org.dto.request.CreateTaxRateRequest;
import com.stockpilot.backend.org.dto.request.UpdateTaxClassRequest;
import com.stockpilot.backend.org.dto.response.TaxBreakdownDto;
import com.stockpilot.backend.org.dto.response.TaxClassDto;
import com.stockpilot.backend.org.dto.response.TaxRateDto;
import com.stockpilot.backend.org.entity.TaxClass;
import com.stockpilot.backend.org.entity.TaxRate;
import com.stockpilot.backend.org.event.TaxConfigChangedEvent;
import com.stockpilot.backend.org.exception.DuplicateTaxClassCodeException;
import com.stockpilot.backend.org.exception.DuplicateTaxClassNameException;
import com.stockpilot.backend.org.exception.InvalidTaxRateException;
import com.stockpilot.backend.org.exception.TaxClassNotFoundException;
import com.stockpilot.backend.org.mapper.TaxMapper;
import com.stockpilot.backend.org.repository.TaxClassRepository;
import com.stockpilot.backend.org.repository.TaxRateRepository;
import com.stockpilot.backend.org.service.calculator.TaxCalculationService;
import com.stockpilot.backend.org.service.TaxService;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaxServiceImpl implements TaxService {

    private final TaxClassRepository taxClassRepository;
    private final TaxRateRepository taxRateRepository;
    private final TaxMapper taxMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ApplicationEventPublisher eventPublisher;
    private final TaxCalculationService taxCalculationService;

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @Transactional(readOnly = true)
    public List<TaxClassDto> getTaxClasses(boolean activeOnly) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        log.debug(
                "Loading tax classes. Tenant={}, ActiveOnly={}",
                tenantId,
                activeOnly
        );

        List<TaxClass> taxClasses =
                taxClassRepository.findByTenantIdAndDeletedFalseOrderByNameAsc(
                        tenantId
                );

        if (activeOnly) {
            LocalDate today = LocalDate.now();

            taxClasses.forEach(taxClass ->
                    taxClass.setRates(
                            taxClass.getRates()
                                    .stream()
                                    .filter(rate -> rate.isEffectiveOn(today))
                                    .toList()
                    )
            );
        }

        return taxClasses.stream()
                .map(taxMapper::toDto)
                .toList();
    }

    @Override
    public TaxClassDto createTaxClass(CreateTaxClassRequest request) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();
        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();

        TaxClass taxClass = taxMapper.toEntity(request);
        taxClass.setTenantId(tenantId);
        normalize(taxClass);

        validateUniqueName(tenantId, request.name());
        validateUniqueCode(tenantId, request.code());




        if (Boolean.TRUE.equals(request.isDefault())) {

            taxClassRepository
                    .findByTenantIdAndDefaultTaxClassTrueAndDeletedFalse(tenantId)
                    .ifPresent(existingDefault -> {
                        existingDefault.removeAsDefault();
                        taxClassRepository.save(existingDefault);
                    });

            taxClass.markAsDefault();
        }

        TaxClass savedTaxClass = taxClassRepository.save(taxClass);

        List<TaxRate> taxRates = request.rates()
                .stream()
                .map(taxMapper::toEntity)
                .peek(rate -> {
                    rate.setTenantId(tenantId);
                    rate.setTaxClass(savedTaxClass);
                })
                .toList();

        taxRateRepository.saveAll(taxRates);

        savedTaxClass.setRates(taxRates);

        eventPublisher.publishEvent(
                TaxConfigChangedEvent.of(
                        savedTaxClass.getId(),
                        tenantId,
                        currentUserId
                )
        );

        log.info(
                "Tax class created. Tenant={}, TaxClass={}, CreatedBy={}",
                tenantId,
                savedTaxClass.getId(),
                currentUserId
        );

        return taxMapper.toDto(savedTaxClass);
    }

    @Override
    public TaxClassDto updateTaxClass(
            UUID taxClassId,
            UpdateTaxClassRequest request
    ) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();
        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();

        TaxClass taxClass = getTaxClassOrThrow(
                tenantId,
                taxClassId
        );

        taxMapper.updateEntityFromRequest(
                request,
                taxClass
        );

        normalize(taxClass);

        validateUniqueName(
                tenantId,
                taxClass.getName(),
                taxClass.getId()
        );

        TaxClass updatedTaxClass =
                taxClassRepository.save(taxClass);

        eventPublisher.publishEvent(
                TaxConfigChangedEvent.of(
                        updatedTaxClass.getId(),
                        tenantId,
                        currentUserId
                )
        );

        log.info(
                "Tax class updated. Tenant={}, TaxClass={}, UpdatedBy={}",
                tenantId,
                updatedTaxClass.getId(),
                currentUserId
        );

        return taxMapper.toDto(updatedTaxClass);
    }

    @Override
    public TaxClassDto setDefaultTaxClass(UUID taxClassId) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();
        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();

        TaxClass taxClass = getTaxClassOrThrow(
                tenantId,
                taxClassId
        );

        if (taxClass.isDefaultTaxClass()) {

            log.debug(
                    "Tax class is already the default. Tenant={}, TaxClass={}",
                    tenantId,
                    taxClassId
            );

            return taxMapper.toDto(taxClass);
        }

        taxClassRepository
                .findByTenantIdAndDefaultTaxClassTrueAndDeletedFalse(tenantId)
                .ifPresent(currentDefault -> {

                    currentDefault.removeAsDefault();
                    entityManager.flush();
                });

        taxClass.markAsDefault();

        TaxClass updatedTaxClass =
                taxClassRepository.save(taxClass);

        eventPublisher.publishEvent(
                TaxConfigChangedEvent.of(
                        updatedTaxClass.getId(),
                        tenantId,
                        currentUserId
                )
        );

        log.info(
                "Default tax class changed. Tenant={}, TaxClass={}, UpdatedBy={}",
                tenantId,
                updatedTaxClass.getId(),
                currentUserId
        );

        return taxMapper.toDto(updatedTaxClass);
    }

    @Override
    public TaxRateDto addTaxRate(
            UUID taxClassId,
            @Valid CreateTaxRateRequest request
    ) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();
        UUID currentUserId = authenticatedUserProvider.getCurrentUserId();

        TaxClass taxClass = getTaxClassOrThrow(
                tenantId,
                taxClassId
        );

        validateFutureEffectiveDate(request.effectiveFrom());

        TaxRate previousRate = taxRateRepository
                .findCurrentRate(
                        taxClassId,
                        request.rateType()
                )
                .orElse(null);

        validateEffectiveDate(
                previousRate,
                request.effectiveFrom()
        );

        TaxRate taxRate = taxMapper.toEntity(request);

        taxRate.setTenantId(tenantId);
        taxRate.setTaxClass(taxClass);

        TaxRate savedRate = taxRateRepository.save(taxRate);

        eventPublisher.publishEvent(
                TaxConfigChangedEvent.of(
                        taxClass.getId(),
                        tenantId,
                        currentUserId
                )
        );

        log.info(
                "New tax rate added. Tenant={}, TaxClass={}, RateType={}, EffectiveFrom={}, UpdatedBy={}",
                tenantId,
                taxClass.getId(),
                savedRate.getRateType(),
                savedRate.getEffectiveFrom(),
                currentUserId
        );

        return taxMapper.toDto(savedRate);
    }

    @Override
    @Transactional(readOnly = true)
    public TaxBreakdownDto resolveTax(
            UUID taxClassId,
            BigDecimal amount,
            LocalDate transactionDate
    ) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        TaxClass taxClass = getTaxClassOrThrow(
                tenantId,
                taxClassId
        );

        List<TaxRate> rates = taxRateRepository.findEffectiveRates(
                taxClassId,
                transactionDate
        );

        return taxCalculationService.calculate(
                taxClass,
                amount,
                rates
        );
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void validateUniqueName(UUID tenantId, String name) {

        if (taxClassRepository.existsByTenantIdAndNameIgnoreCaseAndDeletedFalse(
                tenantId,
                name
        )) {
            throw new DuplicateTaxClassNameException("Tax class name already exists: " + name);
        }
    }

    private void validateUniqueName(
            UUID tenantId,
            String name,
            UUID taxClassId
    ) {

        taxClassRepository
                .findByTenantIdAndNameIgnoreCaseAndDeletedFalse(
                        tenantId,
                        name
                )
                .filter(existing ->
                        !existing.getId().equals(taxClassId)
                )
                .ifPresent(existing -> {
                    throw new DuplicateTaxClassNameException(name);
                });
    }

    private TaxClass getTaxClassOrThrow(
            UUID tenantId,
            UUID taxClassId
    ) {

        log.debug(
                "Loading tax class. Tenant={}, TaxClass={}",
                tenantId,
                taxClassId
        );

        return taxClassRepository
                .findByIdAndTenantIdAndDeletedFalse(
                        taxClassId,
                        tenantId
                )
                .orElseThrow(() ->
                        new TaxClassNotFoundException("Tax class not found with ID: " + taxClassId));
    }

    private void validateUniqueCode(UUID tenantId, String code) {

        if (taxClassRepository.existsByTenantIdAndCodeIgnoreCaseAndDeletedFalse(
                tenantId,
                code
        )) {
            throw new DuplicateTaxClassCodeException("Tax class code already exists: " + code);
        }
    }

    private void validateEffectiveDate(
            TaxRate currentRate,
            LocalDate newEffectiveFrom
    ) {

        if (currentRate != null
                && !newEffectiveFrom.isAfter(currentRate.getEffectiveFrom())) {

            throw new InvalidTaxRateException(
                    "Effective date must be after the current active rate."
            );
        }
    }

    private void validateFutureEffectiveDate(
            LocalDate effectiveFrom
    ) {

        if (!effectiveFrom.isAfter(LocalDate.now())) {
            throw new InvalidTaxRateException(
                    "New tax rates must become effective on a future date."
            );
        }

    }
    private void normalize(TaxClass taxClass) {

        if (taxClass.getName() != null) {
            taxClass.setName(taxClass.getName().trim());
        }

        if (taxClass.getCode() != null) {
            taxClass.setCode(
                    taxClass.getCode()
                            .trim()
                            .toUpperCase(Locale.ROOT)
            );
        }

        if (taxClass.getHsnSacCode() != null) {
            taxClass.setHsnSacCode(
                    taxClass.getHsnSacCode().trim()
            );
        }

        if (taxClass.getDescription() != null) {
            taxClass.setDescription(
                    taxClass.getDescription().trim()
            );
        }
    }
}
