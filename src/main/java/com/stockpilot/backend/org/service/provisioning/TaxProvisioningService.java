package com.stockpilot.backend.org.service.provisioning;

import com.stockpilot.backend.org.entity.TaxClass;
import com.stockpilot.backend.org.entity.TaxRate;
import com.stockpilot.backend.org.enums.RateType;
import com.stockpilot.backend.org.enums.TaxType;
import com.stockpilot.backend.org.repository.TaxClassRepository;
import com.stockpilot.backend.org.repository.TaxRateRepository;
import com.stockpilot.backend.tenant.domain.entity.Tenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaxProvisioningService {

    private final TaxClassRepository taxClassRepository;
    private final TaxRateRepository taxRateRepository;

    @Transactional
    public void provisionDefaults(Tenant tenant) {

        if (taxClassRepository.existsByTenantIdAndDeletedFalse(tenant.getId())) {

            log.debug(
                    "Tax configuration already exists. Tenant={}",
                    tenant.getId()
            );

            return;
        }

        createTaxClass(
                tenant,
                "Zero Rated",
                "GST0",
                BigDecimal.ZERO,
                false
        );

        createTaxClass(
                tenant,
                "GST 5%",
                "GST5",
                BigDecimal.valueOf(2.5),
                false
        );

        createTaxClass(
                tenant,
                "GST 12%",
                "GST12",
                BigDecimal.valueOf(6),
                false
        );

        createTaxClass(
                tenant,
                "GST 18%",
                "GST18",
                BigDecimal.valueOf(9),
                true
        );

        createTaxClass(
                tenant,
                "GST 28%",
                "GST28",
                BigDecimal.valueOf(14),
                false
        );

        log.info(
                "Default GST configuration provisioned. Tenant={}",
                tenant.getId()
        );
    }

    private void createTaxClass(
            Tenant tenant,
            String name,
            String code,
            BigDecimal componentRate,
            boolean defaultClass
    ) {

        TaxClass taxClass = taxClassRepository.save(
                TaxClass.builder()
                        .tenantId(tenant.getId())
                        .name(name)
                        .code(code)
                        .taxType(TaxType.GST)
                        .defaultTaxClass(defaultClass)
                        .build()
        );

        List<TaxRate> rates = List.of(

                TaxRate.builder()
                        .tenantId(tenant.getId())
                        .taxClass(taxClass)
                        .rateType(RateType.CGST)
                        .rate(componentRate)
                        .effectiveFrom(LocalDate.now())
                        .build(),

                TaxRate.builder()
                        .tenantId(tenant.getId())
                        .taxClass(taxClass)
                        .rateType(RateType.SGST)
                        .rate(componentRate)
                        .effectiveFrom(LocalDate.now())
                        .build()
        );

        taxRateRepository.saveAll(rates);
    }
}