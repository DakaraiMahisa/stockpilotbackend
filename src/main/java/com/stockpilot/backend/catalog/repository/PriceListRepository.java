package com.stockpilot.backend.catalog.repository;

import com.stockpilot.backend.catalog.entity.PriceList;
import com.stockpilot.backend.catalog.enums.PriceListType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PriceListRepository
        extends JpaRepository<PriceList, UUID> {

    Optional<PriceList> findByIdAndTenantIdAndDeletedFalse(
            UUID id,
            UUID tenantId
    );

    List<PriceList> findAllByTenantIdAndDeletedFalseOrderByNameAsc(
            UUID tenantId
    );

    List<PriceList> findAllByTenantIdAndPriceListTypeAndDeletedFalseOrderByNameAsc(
            UUID tenantId,
            PriceListType priceListType
    );

    List<PriceList> findAllByTenantIdAndActiveTrueAndDeletedFalseOrderByNameAsc(
            UUID tenantId
    );


    List<PriceList>
    findAllByTenantIdAndPriceListTypeAndActiveTrueAndDeletedFalseOrderByNameAsc(
            UUID tenantId,
            PriceListType priceListType
    );

    Optional<PriceList>
    findByTenantIdAndCodeAndDeletedFalse(
            UUID tenantId,
            String code
    );

    Optional<PriceList>
    findByTenantIdAndDefaultListTrueAndDeletedFalse(
            UUID tenantId
    );

    Page<PriceList>
    findAllByTenantIdAndDeletedFalseOrderByNameAsc(
            UUID tenantId,
            Pageable pageable
    );
    boolean existsByTenantIdAndCodeAndDeletedFalse(
            UUID tenantId,
            String code
    );
}