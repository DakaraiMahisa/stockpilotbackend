package com.stockpilot.backend.catalog.service.impl;

import com.stockpilot.backend.catalog.dto.request.PriceResolveRequest;
import com.stockpilot.backend.catalog.dto.response.PriceResolveResponse;
import com.stockpilot.backend.catalog.entity.PriceList;
import com.stockpilot.backend.catalog.entity.PriceListItem;
import com.stockpilot.backend.catalog.entity.Product;
import com.stockpilot.backend.catalog.entity.ProductVariant;
import com.stockpilot.backend.catalog.repository.PriceListItemRepository;
import com.stockpilot.backend.catalog.repository.PriceListRepository;
import com.stockpilot.backend.catalog.repository.ProductRepository;
import com.stockpilot.backend.catalog.repository.ProductVariantRepository;
import com.stockpilot.backend.catalog.service.PricingService;
import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;
import com.stockpilot.backend.shared.exception.base.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PricingServiceImpl implements PricingService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final PriceListRepository priceListRepository;
    private final PriceListItemRepository priceListItemRepository;

    @Override
    public PriceResolveResponse resolvePrice(
            UUID tenantId,
            PriceResolveRequest request
    ) {

        validateRequest(request);

        LocalDate resolutionDate =
                request.date() != null
                        ? request.date()
                        : LocalDate.now();

        Product product = null;
        ProductVariant variant = null;

        if (request.variantId() != null) {

            variant = productVariantRepository
                    .findByIdAndTenantIdAndDeletedFalse(
                            request.variantId(),
                            tenantId
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product variant not found."
                            )
                    );

            if (!variant.isActive()) {
                throw new BadRequestException(
                        "Product variant is inactive."
                );
            }

            product = variant.getProduct();

        } else {

            product = productRepository
                    .findByIdAndTenantIdAndDeletedFalse(
                            request.productId(),
                            tenantId
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product not found."
                            )
                    );

            if (!product.isActive()) {
                throw new BadRequestException(
                        "Product is inactive."
                );
            }
        }

        PriceList priceList = resolvePriceList(
                tenantId,
                resolutionDate
        );

        PriceListItem priceItem = null;

        /*
         * Variant-specific pricing has priority.
         */
        if (variant != null) {

            priceItem = priceListItemRepository
                    .findFirstByPriceListIdAndVariantIdAndMinQuantityLessThanEqualAndTenantIdAndDeletedFalseOrderByMinQuantityDesc(
                            priceList.getId(),
                            variant.getId(),
                            request.quantity(),
                            tenantId
                    )
                    .orElse(null);
        }

        /*
         * Fall back to product-level pricing.
         */
        if (priceItem == null) {

            priceItem = priceListItemRepository
                    .findFirstByPriceListIdAndProductIdAndMinQuantityLessThanEqualAndTenantIdAndDeletedFalseOrderByMinQuantityDesc(
                            priceList.getId(),
                            product.getId(),
                            request.quantity(),
                            tenantId
                    )
                    .orElse(null);
        }

        if (priceItem == null) {
            throw new ResourceNotFoundException(
                    "No applicable price found."
            );
        }

        BigDecimal discountPct =
                priceItem.getDiscountPct() != null
                        ? priceItem.getDiscountPct()
                        : BigDecimal.ZERO;

        BigDecimal discountMultiplier =
                BigDecimal.ONE.subtract(
                        discountPct.divide(
                                BigDecimal.valueOf(100),
                                10,
                                RoundingMode.HALF_UP
                        )
                );

        BigDecimal effectiveUnitPrice =
                priceItem.getUnitPrice()
                        .multiply(discountMultiplier)
                        .setScale(4, RoundingMode.HALF_UP);

        return new PriceResolveResponse(
                priceList.getId(),
                priceList.getCode(),
                priceList.getCurrencyCode(),
                product.getId(),
                variant != null ? variant.getId() : null,
                request.quantity(),
                priceItem.getMinQuantity(),
                priceItem.getUnitPrice(),
                discountPct,
                effectiveUnitPrice
        );
    }

    private PriceList resolvePriceList(
            UUID tenantId,
            LocalDate date
    ) {

        List<PriceList> priceLists =
                priceListRepository
                        .findAllByTenantIdAndActiveTrueAndDeletedFalseOrderByNameAsc(
                                tenantId
                        );

        return priceLists.stream()
                .filter(priceList -> isValidOn(priceList, date))
                .filter(PriceList::isDefaultList)
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No active default price list is available."
                        )
                );
    }

    private boolean isValidOn(
            PriceList priceList,
            LocalDate date
    ) {

        boolean startsBeforeOrOn =
                priceList.getValidFrom() == null
                        || !priceList.getValidFrom().isAfter(date);

        boolean endsAfterOrOn =
                priceList.getValidTo() == null
                        || !priceList.getValidTo().isBefore(date);

        return startsBeforeOrOn && endsAfterOrOn;
    }

    private void validateRequest(
            PriceResolveRequest request
    ) {

        if (request.productId() == null) {
            throw new BadRequestException(
                    "Product ID is required."
            );
        }

        if (request.quantity() == null
                || request.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(
                    "Quantity must be greater than zero."
            );
        }
    }
}