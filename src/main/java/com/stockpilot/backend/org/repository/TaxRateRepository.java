package com.stockpilot.backend.org.repository;

import com.stockpilot.backend.org.entity.TaxRate;
import com.stockpilot.backend.org.enums.RateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaxRateRepository extends JpaRepository<TaxRate, UUID> {

    List<TaxRate> findByTaxClassIdAndTenantIdAndDeletedFalseOrderByEffectiveFromDesc(
            UUID taxClassId,
            UUID tenantId
    );

    List<TaxRate> findByTaxClassIdAndTenantIdAndEffectiveFromLessThanEqualAndDeletedFalseOrderByEffectiveFromDesc(
            UUID taxClassId,
            UUID tenantId,
            LocalDate effectiveDate
    );

    List<TaxRate> findByTaxClassIdAndRateTypeAndTenantIdAndDeletedFalseOrderByEffectiveFromDesc(
            UUID taxClassId,
            RateType rateType,
            UUID tenantId
    );

    @Query("""
    SELECT tr
    FROM TaxRate tr
    WHERE tr.taxClass.id = :taxClassId
      AND tr.rateType = :rateType
      AND tr.effectiveTo IS NULL
      AND tr.deleted = false
    """)
    Optional<TaxRate> findCurrentRate(
            UUID taxClassId,
            RateType rateType
    );

    @Query("""
    SELECT tr
    FROM TaxRate tr
    WHERE tr.taxClass.id = :taxClassId
      AND tr.deleted = false
      AND tr.effectiveFrom <= :transactionDate
      AND (
            tr.effectiveTo IS NULL
            OR tr.effectiveTo >= :transactionDate
      )
    ORDER BY tr.rateType
    """)
    List<TaxRate> findEffectiveRates(
            UUID taxClassId,
            LocalDate transactionDate
    );
}