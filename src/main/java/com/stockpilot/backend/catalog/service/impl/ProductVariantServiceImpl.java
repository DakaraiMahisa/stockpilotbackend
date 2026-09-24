package com.stockpilot.backend.catalog.service.impl;

import com.stockpilot.backend.catalog.dto.request.CreateProductVariantRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateProductVariantRequest;
import com.stockpilot.backend.catalog.dto.response.ProductVariantDto;
import com.stockpilot.backend.catalog.dto.response.VariantAttributeDto;
import com.stockpilot.backend.catalog.dto.response.VariantAttributeValueDto;
import com.stockpilot.backend.catalog.entity.*;

import com.stockpilot.backend.catalog.mapper.ProductVariantMapper;
import com.stockpilot.backend.catalog.repository.*;
import com.stockpilot.backend.catalog.service.ProductVariantService;

import com.stockpilot.backend.shared.exception.base.BusinessException;
import com.stockpilot.backend.shared.exception.base.BusinessRuleException;
import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantAttributeRepository productVariantAttributeRepository;
    private final VariantAttributeValueRepository variantAttributeValueRepository;
    private final VariantAttributeRepository variantAttributeRepository;
    private final ProductVariantMapper productVariantMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public List<ProductVariantDto> createVariants(
            UUID productId,
            List<CreateProductVariantRequest> requests
    ) {
        UUID tenantId = getTenantId();

        Product product = getProduct(productId);

        validateRequests(requests);

        List<ProductVariant> variants = new ArrayList<>();

        for (CreateProductVariantRequest request : requests) {

            validateAttributeCombination(
                    productId,
                    request.attributes()
            );

            validatePrices(
                    request.costPrice(),
                    request.retailPrice()
            );

            ensureCombinationDoesNotExist(
                    productId,
                    request.attributes()
            );

            String sku = generateVariantSku(
                    product,
                    request.attributes()
            );

            ProductVariant variant = ProductVariant.builder()
                    .tenantId(tenantId)
                    .product(product)
                    .sku(sku)
                    .barcode(request.barcode())
                    .attributes(request.attributes())
                    .costPrice(request.costPrice())
                    .retailPrice(request.retailPrice())
                    .imageUrl(request.imageUrl())
                    .additionalWeightKg(
                            request.additionalWeightKg() != null
                                    ? request.additionalWeightKg()
                                    : BigDecimal.ZERO
                    )
                    .active(true)
                    .build();

            variants.add(variant);
        }

        List<ProductVariant> savedVariants =
                productVariantRepository.saveAll(variants);

        if (!product.isHasVariants()) {
            product.setHasVariants(true);
            productRepository.save(product);
        }

        return savedVariants.stream()
                .map(productVariantMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantDto> getVariantsByProductId(
            UUID productId
    ) {

        getProduct(productId);

        return productVariantRepository
                .findAllByProductIdAndTenantIdAndDeletedFalse(
                        productId,
                        getTenantId()
                )
                .stream()
                .map(productVariantMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantDto getVariantById(
            UUID productId,
            UUID variantId
    ) {

        ProductVariant variant =
                getVariant(productId, variantId);

        return productVariantMapper.toDto(variant);
    }

    @Override
    @Transactional
    public ProductVariantDto updateVariant(
            UUID productId,
            UUID variantId,
            UpdateProductVariantRequest request
    ) {

        ProductVariant variant =
                getVariant(productId, variantId);

        if (request.barcode() != null) {
            variant.setBarcode(request.barcode());
        }

        if (request.costPrice() != null) {
            variant.setCostPrice(request.costPrice());
        }

        if (request.retailPrice() != null) {
            variant.setRetailPrice(request.retailPrice());
        }

        if (request.imageUrl() != null) {
            variant.setImageUrl(request.imageUrl());
        }

        if (request.additionalWeightKg() != null) {
            variant.setAdditionalWeightKg(
                    request.additionalWeightKg()
            );
        }

        if (request.active() != null) {
            variant.setActive(request.active());
        }

        validatePrices(
                variant.getCostPrice(),
                variant.getRetailPrice()
        );

        ProductVariant updated =
                productVariantRepository.save(variant);

        return productVariantMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deactivateVariant(
            UUID productId,
            UUID variantId
    ) {

        UUID tenantId = getTenantId();

        ProductVariant variant =
                productVariantRepository
                        .findByIdAndProductIdAndTenantIdAndDeletedFalse(
                                variantId,
                                productId,
                                tenantId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product variant not found"
                                )
                        );

        if (!variant.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException(
                    "Product variant not found"
            );
        }

        if (!variant.isActive()) {
            throw new BusinessRuleException(
                    "Product variant is already inactive"
            );
        }

        variant.setActive(false);

        productVariantRepository.save(variant);
    }

    @Override
    @Transactional
    public void deleteVariant(
            UUID productId,
            UUID variantId
    ) {

        ProductVariant variant =
                getVariant(productId, variantId);

        if (!variant.isActive()) {
            throw new BusinessRuleException(
                    "Cannot delete an inactive product variant"
            );
        }

        /*
         * Inventory and open-order checks will be added when
         * those modules/entities are implemented.
         */

        variant.setDeleted(true);
        variant.setActive(false);

        productVariantRepository.save(variant);
    }


    @Override
    @Transactional(readOnly = true)
    public List<VariantAttributeDto> getVariantAttributes(
            UUID productId
    ) {

        Product product = getProduct(productId);

        UUID tenantId = getTenantId();

        return productVariantAttributeRepository
                .findAllByProductIdAndTenantIdOrderBySortOrderAsc(
                        product.getId(),
                        tenantId
                )
                .stream()
                .map(productVariantAttribute -> {

                    VariantAttribute attribute =
                            productVariantAttribute.getAttribute();

                    List<VariantAttributeValueDto> values =
                            variantAttributeValueRepository
                                    .findAllByAttributeIdAndTenantIdAndActiveTrueOrderBySortOrderAsc(
                                            attribute.getId(),
                                            tenantId
                                    )
                                    .stream()
                                    .map(value ->
                                            new VariantAttributeValueDto(
                                                    value.getId(),
                                                    attribute.getId(),
                                                    value.getValue(),
                                                    value.getCode(),
                                                    value.isActive(),
                                                    value.getSortOrder()
                                            )
                                    )
                                    .toList();

                    return new VariantAttributeDto(
                            attribute.getId(),
                            attribute.getName(),
                            attribute.getCode(),
                            attribute.getDescription(),
                            attribute.isActive(),
                            values
                    );
                })
                .toList();
    }


    @Override
    @Transactional
    public void assignVariantAttribute(
            UUID productId,
            UUID attributeId
    ) {
        UUID tenantId = getTenantId();

        Product product = productRepository
                .findByIdAndTenantIdAndDeletedFalse(productId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found"
                ));

        VariantAttribute attribute = variantAttributeRepository
                .findByIdAndTenantId(attributeId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Variant attribute not found"
                ));

        if (!attribute.isActive()) {
            throw new BusinessException(
                    "Cannot assign an inactive variant attribute"
            );
        }

        boolean alreadyAssigned =
                productVariantAttributeRepository
                        .existsByProductIdAndAttributeIdAndTenantId(
                                productId,
                                attributeId,
                                tenantId
                        );

        if (alreadyAssigned) {
            throw new BusinessException(
                    "Variant attribute is already assigned to this product"
            );
        }

        int nextSortOrder =
                productVariantAttributeRepository
                        .findAllByProductIdAndTenantIdOrderBySortOrderAsc(
                                productId,
                                tenantId
                        )
                        .stream()
                        .mapToInt(ProductVariantAttribute::getSortOrder)
                        .max()
                        .orElse(-1) + 1;

        ProductVariantAttribute assignment =
                ProductVariantAttribute.builder()
                        .tenantId(tenantId)
                        .product(product)
                        .attribute(attribute)
                        .sortOrder(nextSortOrder)
                        .build();

        productVariantAttributeRepository.save(assignment);
    }

    @Override
    @Transactional
    public void removeVariantAttribute(
            UUID productId,
            UUID attributeId
    ) {
        UUID tenantId = getTenantId();

        if (productRepository
                .findByIdAndTenantIdAndDeletedFalse(productId, tenantId)
                .isEmpty()) {

            throw new ResourceNotFoundException(
                    "Product not found"
            );
        }

        if (variantAttributeRepository
                .findByIdAndTenantId(attributeId, tenantId)
                .isEmpty()) {

            throw new ResourceNotFoundException(
                    "Variant attribute not found"
            );
        }

        boolean assigned =
                productVariantAttributeRepository
                        .existsByProductIdAndAttributeIdAndTenantId(
                                productId,
                                attributeId,
                                tenantId
                        );

        if (!assigned) {
            throw new ResourceNotFoundException(
                    "Variant attribute is not assigned to this product"
            );
        }

        productVariantAttributeRepository
                .deleteByProductIdAndAttributeIdAndTenantId(
                        productId,
                        attributeId,
                        tenantId
                );
    }

    private Product getProduct(UUID productId) {

        UUID tenantId = getTenantId();

        return productRepository
                .findByIdAndTenantIdAndDeletedFalse(
                        productId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found"
                        )
                );
    }

    private ProductVariant getVariant(
            UUID productId,
            UUID variantId
    ) {

        UUID tenantId = getTenantId();

        return productVariantRepository
                .findByIdAndProductIdAndTenantIdAndDeletedFalse(
                        variantId,
                        productId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product variant not found"
                        )
                );
    }

    private void validateRequests(
            List<CreateProductVariantRequest> requests
    ) {

        if (requests == null || requests.isEmpty()) {
            throw new BusinessRuleException(
                    "At least one variant is required"
            );
        }

        Set<Map<String, String>> combinations = new HashSet<>();

        for (CreateProductVariantRequest request : requests) {

            if (request == null) {
                throw new BusinessRuleException(
                        "Variant request cannot be null"
                );
            }

            if (request.attributes() == null
                    || request.attributes().isEmpty()) {

                throw new BusinessRuleException(
                        "Variant must contain at least one attribute"
                );
            }

            if (!combinations.add(request.attributes())) {
                throw new BusinessRuleException(
                        "Duplicate variant attribute combination in request"
                );
            }
        }
    }

    private void validatePrices(
            BigDecimal costPrice,
            BigDecimal retailPrice
    ) {

        if (costPrice == null || retailPrice == null) {
            throw new BusinessRuleException(
                    "Cost price and retail price are required"
            );
        }

        if (costPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException(
                    "Cost price cannot be negative"
            );
        }

        if (retailPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException(
                    "Retail price cannot be negative"
            );
        }

        if (retailPrice.compareTo(costPrice) < 0) {
            throw new BusinessRuleException(
                    "Retail price cannot be less than cost price"
            );
        }
    }

    private void validateAttributeCombination(
            UUID productId,
            Map<String, String> attributes
    ) {

        if (attributes == null || attributes.isEmpty()) {
            throw new BusinessRuleException(
                    "At least one attribute is required"
            );
        }

        UUID tenantId = getTenantId();

        List<ProductVariantAttribute> configuredAttributes =
                productVariantAttributeRepository
                        .findAllByProductIdAndTenantIdOrderBySortOrderAsc(
                                productId,
                                tenantId
                        );

        if (configuredAttributes.isEmpty()) {
            throw new BusinessRuleException(
                    "No variant attributes are configured for this product"
            );
        }

        Map<String, ProductVariantAttribute> configuredByCode =
                new HashMap<>();

        for (ProductVariantAttribute configuredAttribute :
                configuredAttributes) {

            VariantAttribute attribute =
                    configuredAttribute.getAttribute();

            String attributeCode =
                    attribute.getCode().trim().toUpperCase(Locale.ROOT);

            if (configuredByCode.put(
                    attributeCode,
                    configuredAttribute
            ) != null) {

                throw new BusinessRuleException(
                        "Duplicate attribute configuration: "
                                + attributeCode
                );
            }
        }

        /*
         * Validate supplied attributes.
         */
        for (Map.Entry<String, String> entry :
                attributes.entrySet()) {

            String attributeCode =
                    normalizeCode(entry.getKey());

            String valueCode =
                    normalizeCode(entry.getValue());

            if (attributeCode == null) {
                throw new BusinessRuleException(
                        "Attribute code cannot be blank"
                );
            }

            if (valueCode == null) {
                throw new BusinessRuleException(
                        "Value cannot be blank for attribute: "
                                + attributeCode
                );
            }

            ProductVariantAttribute configured =
                    configuredByCode.get(attributeCode);

            if (configured == null) {
                throw new BusinessRuleException(
                        "Attribute is not configured for this product: "
                                + attributeCode
                );
            }

            VariantAttribute attribute =
                    configured.getAttribute();

            if (!attribute.isActive()) {
                throw new BusinessRuleException(
                        "Attribute is inactive: "
                                + attributeCode
                );
            }

            VariantAttributeValue value =
                    variantAttributeValueRepository
                            .findByAttributeIdAndCodeAndTenantId(
                                    attribute.getId(),
                                    valueCode,
                                    tenantId
                            )
                            .orElseThrow(() ->
                                    new BusinessRuleException(
                                            "Invalid value '"
                                                    + valueCode
                                                    + "' for attribute '"
                                                    + attributeCode
                                                    + "'"
                                    )
                            );

            if (!value.isActive()) {
                throw new BusinessRuleException(
                        "Value '"
                                + valueCode
                                + "' is inactive for attribute '"
                                + attributeCode
                                + "'"
                );
            }
        }

        /*
         * Every configured attribute must be supplied.
         */
        if (attributes.size() != configuredByCode.size()) {
            throw new BusinessRuleException(
                    "All configured variant attributes are required"
            );
        }

        for (String configuredCode :
                configuredByCode.keySet()) {

            if (!attributes.keySet().stream()
                    .map(this::normalizeCode)
                    .anyMatch(configuredCode::equals)) {

                throw new BusinessRuleException(
                        "Missing required variant attribute: "
                                + configuredCode
                );
            }
        }
    }

    private void ensureCombinationDoesNotExist(
            UUID productId,
            Map<String, String> attributes
    ) {
        UUID tenantId = getTenantId();

        String attributesJson =
                objectMapper.writeValueAsString(attributes);

        boolean exists =
                productVariantRepository
                        .existsByProductIdAndTenantIdAndAttributesAndDeletedFalse(
                                productId,
                                tenantId,
                                attributesJson
                        );

        if (exists) {
            throw new BusinessRuleException(
                    "A variant with the same attribute combination already exists"
            );
        }
    }

    private String generateVariantSku(
            Product product,
            Map<String, String> attributes
    ) {

        String combination = attributes.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry ->
                        entry.getKey() + "-" + entry.getValue()
                )
                .collect(Collectors.joining("-"));

        String normalized =
                combination
                        .toUpperCase(Locale.ROOT)
                        .replaceAll("[^A-Z0-9-]", "");

        return product.getSku() + "-" + normalized;
    }

    private UUID getTenantId() {

        // Replace with your existing AuthenticatedUserProvider /
        // TenantContext implementation.
        return authenticatedUserProvider
                .getCurrentTenantId();
    }
    private String generateAttributeHash(
            Map<String, String> attributes
    ) {

        String canonical =
                attributes.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry ->
                                entry.getKey().trim().toLowerCase(Locale.ROOT)
                                        + "="
                                        + entry.getValue().trim().toLowerCase(Locale.ROOT)
                        )
                        .collect(Collectors.joining("|"));

        return DigestUtils
                .sha256Hex(canonical);
    }

    private String normalizeCode(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim().toUpperCase(Locale.ROOT);
    }
}