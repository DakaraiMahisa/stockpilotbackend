package com.stockpilot.backend.org.service.impl;

import com.stockpilot.backend.org.dto.request.BusinessConfigUpdateRequest;
import com.stockpilot.backend.org.dto.response.BusinessConfigDto;
import com.stockpilot.backend.org.entity.BusinessConfig;
import com.stockpilot.backend.org.event.BusinessConfigChangedEvent;
import com.stockpilot.backend.org.exception.BusinessConfigNotFoundException;
import com.stockpilot.backend.org.mapper.BusinessConfigMapper;
import com.stockpilot.backend.org.repository.BusinessConfigRepository;
import com.stockpilot.backend.org.service.BusinessConfigService;
import com.stockpilot.backend.shared.constants.CacheNames;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BusinessConfigServiceImpl implements BusinessConfigService {

    private final BusinessConfigRepository businessConfigRepository;
    private final BusinessConfigMapper businessConfigMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = CacheNames.BUSINESS_CONFIG,
            key =  "@cacheKeys.tenant()"
    )
    public BusinessConfigDto getConfiguration() {
        return businessConfigMapper.toDto(getCurrentBusinessConfig());
    }

    @Override
    @Transactional
    @CacheEvict(
            cacheNames = CacheNames.BUSINESS_CONFIG,
            key = "@cacheKeys.tenant()"
    )
    public BusinessConfigDto updateConfiguration(
            BusinessConfigUpdateRequest request
    ) {

        BusinessConfig businessConfig = getCurrentBusinessConfig();

        businessConfigMapper.updateEntityFromRequest(
                request,
                businessConfig
        );

        normalize(businessConfig);

        BusinessConfig updatedConfig =
                businessConfigRepository.save(businessConfig);
     UUID currentUserId =  authenticatedUserProvider.getCurrentUserId();

        eventPublisher.publishEvent(
                BusinessConfigChangedEvent.of(
                        updatedConfig.getId(),
                        updatedConfig.getTenantId(),
                        currentUserId
                )
        );

        log.info(
                "Business configuration updated. Tenant={}, BusinessConfig={}, UpdatedBy={}",
                updatedConfig.getTenantId(),
                updatedConfig.getId(),
              currentUserId
        );

        return businessConfigMapper.toDto(updatedConfig);
    }

    @Transactional(readOnly = true)
    private BusinessConfig getCurrentBusinessConfig() {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        log.debug(
                "Loading business configuration from database. Tenant={}",
                tenantId
        );


        return businessConfigRepository.findByTenantId(tenantId)
                .orElseThrow(() ->
                        new BusinessConfigNotFoundException(tenantId));
    }

    private void normalize(BusinessConfig config) {

        if (config.getTimezone() != null) {
            config.setTimezone(config.getTimezone().trim());
        }

        if (config.getCurrencyCode() != null) {
            config.setCurrencyCode(
                    config.getCurrencyCode()
                            .trim()
                            .toUpperCase(Locale.ROOT)
            );
        }

        if (config.getCurrencySymbol() != null) {
            config.setCurrencySymbol(
                    config.getCurrencySymbol().trim()
            );
        }

        if (config.getDateFormat() != null) {
            config.setDateFormat(
                    config.getDateFormat().trim()
            );
        }

        if (config.getFiscalYearStart() != null) {
            config.setFiscalYearStart(
                    config.getFiscalYearStart().trim()
            );
        }

        if (config.getDefaultLanguage() != null) {
            config.setDefaultLanguage(
                    config.getDefaultLanguage().trim()
            );
        }
    }


}