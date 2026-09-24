package com.stockpilot.backend.catalog.repository;

import com.stockpilot.backend.catalog.entity.PriceListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PriceListItemRepository
        extends JpaRepository<PriceListItem, UUID> {

    List<PriceListItem>
    findAllByPriceListIdAndTenantIdAndDeletedFalse(
            UUID priceListId,
            UUID tenantId
    );

    List<PriceListItem>
    findAllByPriceListIdAndTenantIdAndDeletedFalseOrderByMinQuantityDesc(
            UUID priceListId,
            UUID tenantId
    );

    Optional<PriceListItem>
    findByIdAndTenantIdAndDeletedFalse(
            UUID id,
            UUID tenantId
    );

    List<PriceListItem>
    findAllByProductIdAndTenantIdAndDeletedFalse(
            UUID productId,
            UUID tenantId
    );

    List<PriceListItem>
    findAllByVariantIdAndTenantIdAndDeletedFalse(
            UUID variantId,
            UUID tenantId
    );

    List<PriceListItem>
    findAllByPriceListIdAndProductIdAndTenantIdAndDeletedFalseOrderByMinQuantityDesc(
            UUID priceListId,
            UUID productId,
            UUID tenantId
    );

    List<PriceListItem>
    findAllByPriceListIdAndVariantIdAndTenantIdAndDeletedFalseOrderByMinQuantityDesc(
            UUID priceListId,
            UUID variantId,
            UUID tenantId
    );

    Optional<PriceListItem>
    findByPriceListIdAndProductIdAndMinQuantityAndTenantIdAndDeletedFalse(
            UUID priceListId,
            UUID productId,
            BigDecimal minQuantity,
            UUID tenantId
    );

    Optional<PriceListItem>
    findByPriceListIdAndVariantIdAndMinQuantityAndTenantIdAndDeletedFalse(
            UUID priceListId,
            UUID variantId,
            BigDecimal minQuantity,
            UUID tenantId
    );

    Optional<PriceListItem>
    findFirstByPriceListIdAndVariantIdAndMinQuantityLessThanEqualAndTenantIdAndDeletedFalseOrderByMinQuantityDesc(
            UUID priceListId,
            UUID variantId,
            BigDecimal quantity,
            UUID tenantId
    );

    Optional<PriceListItem>
    findFirstByPriceListIdAndProductIdAndMinQuantityLessThanEqualAndTenantIdAndDeletedFalseOrderByMinQuantityDesc(
            UUID priceListId,
            UUID productId,
            BigDecimal quantity,
            UUID tenantId
    );

    Optional<PriceListItem> findByIdAndTenantIdAndPriceListIdAndDeletedFalse(
            UUID id,
            UUID tenantId,
            UUID priceListId
    );

}