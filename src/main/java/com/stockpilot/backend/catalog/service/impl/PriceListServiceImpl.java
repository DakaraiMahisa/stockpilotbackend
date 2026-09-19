package com.stockpilot.backend.catalog.service.impl;

import com.stockpilot.backend.catalog.dto.request.CreatePriceListRequest;
import com.stockpilot.backend.catalog.dto.request.PriceListItemRequest;
import com.stockpilot.backend.catalog.dto.request.UpdatePriceListRequest;
import com.stockpilot.backend.catalog.dto.response.PriceListDto;
import com.stockpilot.backend.catalog.dto.response.PriceListItemDto;
import com.stockpilot.backend.catalog.entity.PriceList;
import com.stockpilot.backend.catalog.entity.PriceListItem;
import com.stockpilot.backend.catalog.entity.Product;
import com.stockpilot.backend.catalog.entity.ProductVariant;
import com.stockpilot.backend.catalog.repository.PriceListItemRepository;
import com.stockpilot.backend.catalog.repository.PriceListRepository;
import com.stockpilot.backend.catalog.repository.ProductRepository;
import com.stockpilot.backend.catalog.repository.ProductVariantRepository;
import com.stockpilot.backend.catalog.service.PriceListService;
import com.stockpilot.backend.org.entity.BusinessConfig;
import com.stockpilot.backend.org.repository.BusinessConfigRepository;
import com.stockpilot.backend.shared.exception.base.DuplicateResourceException;
import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;
import com.stockpilot.backend.shared.exception.base.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PriceListServiceImpl implements PriceListService {

    private final PriceListRepository priceListRepository;
    private final PriceListItemRepository priceListItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final BusinessConfigRepository businessConfigRepository;

    @Override
    public PriceListDto create(
            UUID tenantId,
            CreatePriceListRequest request
    ){
        validateDates(request.validFrom(), request.validTo());

        if (priceListRepository.existsByTenantIdAndCodeAndDeletedFalse(
                tenantId,
                request.code()
        )) {
            throw new DuplicateResourceException(
                    "A price list with code '" + request.code() + "' already exists."
            );
        }

        BusinessConfig businessConfig =
                businessConfigRepository.findByTenantId(tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Business configuration not found."
                                )
                        );

        if (request.defaultList()) {
            clearExistingDefault(tenantId);
        }

        PriceList priceList = PriceList.builder()
                .name(request.name().trim())
                .code(request.code().trim().toUpperCase())
                .priceListType(request.priceListType())
                .currencyCode(businessConfig.getCurrencyCode())
                .defaultList(request.defaultList())
                .validFrom(request.validFrom())
                .validTo(request.validTo())
                .active(request.active())
                .build();

        PriceList saved = priceListRepository.save(priceList);

        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PriceListDto getById(
            UUID tenantId,
            UUID priceListId
    ){
        PriceList priceList = getPriceList(tenantId, priceListId);

        return toDto(priceList);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PriceListDto> getAll(
            UUID tenantId,
            Pageable pageable
    ){
        return priceListRepository
                .findAllByTenantIdAndDeletedFalseOrderByNameAsc(
                        tenantId,
                        pageable
                )
                .map(this::toDto);
    }

    @Override
    public PriceListDto update(
            UUID tenantId,
            UUID priceListId,
            UpdatePriceListRequest request
    ) {
        validateDates(request.validFrom(), request.validTo());

        PriceList priceList = getPriceList(tenantId, priceListId);

        if (request.defaultList()) {
            clearExistingDefault(tenantId, priceListId);
        }

        priceList.setName(request.name().trim());
        priceList.setPriceListType(request.priceListType());
        priceList.setDefaultList(request.defaultList());
        priceList.setValidFrom(request.validFrom());
        priceList.setValidTo(request.validTo());
        priceList.setActive(request.active());

        return toDto(priceListRepository.save(priceList));
    }

    @Override
    public void delete(
            UUID tenantId,
            UUID priceListId
    ) {
        PriceList priceList = getPriceList(tenantId, priceListId);

        priceList.setActive(false);
        priceList.setDeleted(true);

        List<PriceListItem> items =
                priceListItemRepository
                        .findAllByPriceListIdAndTenantIdAndDeletedFalse(
                                priceListId,
                                tenantId
                        );

        items.forEach(item -> item.setDeleted(true));

        priceListItemRepository.saveAll(items);
        priceListRepository.save(priceList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceListItemDto> getItems(
            UUID tenantId,
            UUID priceListId
    ) {
        getPriceList(tenantId, priceListId);

        return priceListItemRepository
                .findAllByPriceListIdAndTenantIdAndDeletedFalseOrderByMinQuantityDesc(
                        priceListId,
                        tenantId
                )
                .stream()
                .map(this::toItemDto)
                .toList();
    }

    @Override
    public List<PriceListItemDto> upsertItems(
            UUID tenantId,
            UUID priceListId,
            List<PriceListItemRequest> requests
    ){
        PriceList priceList = getPriceList(tenantId, priceListId);

        if (requests == null || requests.isEmpty()) {
            throw new BadRequestException(
                    "At least one price list item is required."
            );
        }

        List<PriceListItem> items = new ArrayList<>();

        for (PriceListItemRequest request : requests) {

            validateItemTarget(request);

            Product product = productRepository
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
                        "Cannot add a price for an inactive product."
                );
            }

            ProductVariant variant = null;

            if (request.variantId() != null) {

                variant = productVariantRepository
                        .findByIdAndProductIdAndTenantIdAndDeletedFalse(
                                request.variantId(),
                                request.productId(),
                                tenantId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant not found for the specified product."
                                )
                        );

                if (!variant.isActive()) {
                    throw new BadRequestException(
                            "Cannot add a price for an inactive variant."
                    );
                }
            }

            validateDiscount(request.discountPct());

            PriceListItem item = findExistingItem(
                    priceListId,
                    tenantId,
                    request
            ).orElseGet(PriceListItem::new);

            item.setTenantId(tenantId);
            item.setPriceList(priceList);
            item.setProduct(product);
            item.setVariant(variant);
            item.setMinQuantity(request.minQuantity());
            item.setUnitPrice(request.unitPrice());
            item.setDiscountPct(
                    request.discountPct() != null
                            ? request.discountPct()
                            : BigDecimal.ZERO
            );

            items.add(item);
        }

        return priceListItemRepository.saveAll(items)
                .stream()
                .map(this::toItemDto)
                .toList();
    }

    private PriceList getPriceList(
            UUID tenantId,
            UUID priceListId
    ) {
        return priceListRepository
                .findByIdAndTenantIdAndDeletedFalse(
                        priceListId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Price list not found."
                        )
                );
    }

    private Optional<PriceListItem> findExistingItem(
            UUID priceListId,
            UUID tenantId,
            PriceListItemRequest request
    ){
        if (request.variantId() != null) {
            return priceListItemRepository
                    .findByPriceListIdAndVariantIdAndMinQuantityAndTenantIdAndDeletedFalse(
                            priceListId,
                            request.variantId(),
                            request.minQuantity(),
                            tenantId
                    );
        }

        return priceListItemRepository
                .findByPriceListIdAndProductIdAndMinQuantityAndTenantIdAndDeletedFalse(
                        priceListId,
                        request.productId(),
                        request.minQuantity(),
                        tenantId
                );
    }

    private void validateItemTarget(
            PriceListItemRequest request
    ){
        if (request.productId() == null) {
            throw new BadRequestException(
                    "productId is required."
            );
        }
    }

    private void validateDates(
            LocalDate validFrom,
            LocalDate validTo
    ) {
        if (validFrom != null
                && validTo != null
                && validTo.isBefore(validFrom)) {

            throw new BadRequestException(
                    "validTo cannot be before validFrom."
            );
        }
    }

    private void validateDiscount(
            BigDecimal discountPct
    ){
        if (discountPct == null) {
            return;
        }

        if (discountPct.compareTo(BigDecimal.ZERO) < 0
                || discountPct.compareTo(BigDecimal.valueOf(100)) > 0) {

            throw new BadRequestException(
                    "Discount percentage must be between 0 and 100."
            );
        }
    }

    private void clearExistingDefault(UUID tenantId) {
        clearExistingDefault(tenantId, null);
    }

    private void clearExistingDefault(
            UUID tenantId,
            UUID currentPriceListId
    ) {
        priceListRepository
                .findByTenantIdAndDefaultListTrueAndDeletedFalse(tenantId)
                .ifPresent(existing -> {

                    if (currentPriceListId == null
                            || !existing.getId().equals(currentPriceListId)) {

                        existing.setDefaultList(false);
                        priceListRepository.save(existing);
                    }
                });
    }

    private PriceListDto toDto(PriceList priceList) {
        return new PriceListDto(
                priceList.getId(),
                priceList.getName(),
                priceList.getCode(),
                priceList.getPriceListType(),
                priceList.getCurrencyCode(),
                priceList.isDefaultList(),
                priceList.getValidFrom(),
                priceList.getValidTo(),
                priceList.isActive(),
                priceList.getCreatedAt(),
                priceList.getUpdatedAt()
        );
    }

    private PriceListItemDto toItemDto(
            PriceListItem item
    ){
        return new PriceListItemDto(
                item.getId(),
                item.getPriceList().getId(),
                item.getProduct().getId(),
                item.getVariant() != null
                        ? item.getVariant().getId()
                        : null,
                item.getMinQuantity(),
                item.getUnitPrice(),
                item.getDiscountPct()
        );
    }
}
