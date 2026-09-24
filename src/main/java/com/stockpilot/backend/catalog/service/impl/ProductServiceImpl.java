package com.stockpilot.backend.catalog.service.impl;

import com.stockpilot.backend.catalog.config.ProductRabbitConfig;
import com.stockpilot.backend.catalog.dto.request.CreateProductRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateProductRequest;
import com.stockpilot.backend.catalog.dto.response.ProductDto;
import com.stockpilot.backend.catalog.entity.Brand;
import com.stockpilot.backend.catalog.entity.Category;
import com.stockpilot.backend.catalog.entity.Product;
import com.stockpilot.backend.catalog.event.ProductCreatedEvent;
import com.stockpilot.backend.catalog.event.ProductUpdatedEvent;
import com.stockpilot.backend.catalog.mapper.ProductMapper;
import com.stockpilot.backend.catalog.repository.BrandRepository;
import com.stockpilot.backend.catalog.repository.CategoryRepository;
import com.stockpilot.backend.catalog.repository.ProductRepository;
import com.stockpilot.backend.catalog.service.ProductService;
import com.stockpilot.backend.catalog.service.SkuSequenceService;
import com.stockpilot.backend.org.entity.TaxClass;
import com.stockpilot.backend.org.repository.TaxClassRepository;

import com.stockpilot.backend.shared.exception.base.BusinessException;
import com.stockpilot.backend.shared.exception.base.DuplicateResourceException;
import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final TaxClassRepository taxClassRepository;
    private final SkuSequenceService skuSequenceService;
    private final ProductMapper productMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Category category = getCategoryEntity(
                request.categoryId(),
                tenantId
        );

        validateLeafCategory(category);

        Brand brand = null;

        if (request.brandId() != null) {
            brand = getBrandEntity(
                    request.brandId(),
                    tenantId
            );
        }

        TaxClass taxClass = getTaxClassEntity(
                request.taxClassId(),
                tenantId
        );

        validateSku(
                request.sku(),
                tenantId
        );

        validateBarcode(
                request.barcode(),
                tenantId
        );

        validateStockLevelRange(
                request.minStockLevel(),
                request.maxStockLevel()
        );

        Product product = productMapper.toEntity(request);

        product.setTenantId(tenantId);

        product.setCategory(category);
        product.setBrand(brand);
        product.setTaxClass(taxClass);

        if (request.sku() == null || request.sku().isBlank()) {
            product.setSku(
                    generateSku(
                            brand,
                            category,
                            tenantId
                    )
            );
        }

        if (request.trackBatches() == null) {
            product.setTrackBatches(false);
        }

        if (request.service() == null) {
            product.setService(false);
        }

        if (request.minStockLevel() == null) {
            product.setMinStockLevel(0);
        }

        product.setHasVariants(false);
        product.setActive(true);

        Product savedProduct = productRepository.save(product);

        rabbitTemplate.convertAndSend(
                ProductRabbitConfig.PRODUCT_EXCHANGE,
                ProductRabbitConfig.PRODUCT_CREATED_ROUTING_KEY,
                ProductCreatedEvent.builder()
                        .productId(savedProduct.getId())
                        .tenantId(savedProduct.getTenantId())
                        .sku(savedProduct.getSku())
                        .name(savedProduct.getName())
                        .build()
        );

        return productMapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(
            UUID productId,
            UpdateProductRequest request
    ) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Product product = getProductEntity(
                productId,
                tenantId
        );

        Category category = null;

        if (request.categoryId() != null) {
            category = getCategoryEntity(
                    request.categoryId(),
                    tenantId
            );

            validateLeafCategory(category);
        }

        Brand brand = null;

        if (request.brandId() != null) {
            brand = getBrandEntity(
                    request.brandId(),
                    tenantId
            );
        }

        TaxClass taxClass = null;

        if (request.taxClassId() != null) {
            taxClass = getTaxClassEntity(
                    request.taxClassId(),
                    tenantId
            );
        }

        Integer effectiveMinStockLevel =
                request.minStockLevel() != null
                        ? request.minStockLevel()
                        : product.getMinStockLevel();

        Integer effectiveMaxStockLevel =
                request.maxStockLevel() != null
                        ? request.maxStockLevel()
                        : product.getMaxStockLevel();

        validateStockLevelRange(
                effectiveMinStockLevel,
                effectiveMaxStockLevel
        );

        productMapper.updateEntityFromRequest(
                request,
                product
        );

        if (request.barcode() != null) {
            product.setBarcode(request.barcode());
        }

        if (request.unitOfMeasure() != null) {
            product.setUnitOfMeasure(request.unitOfMeasure());
        }

        if (request.active() != null) {
            product.setActive(request.active());
        }

        if (category != null) {
            product.setCategory(category);
        }

        if (request.brandId() != null) {
            product.setBrand(brand);
        }

        if (taxClass != null) {
            product.setTaxClass(taxClass);
        }

        Product updatedProduct = productRepository.save(product);

        rabbitTemplate.convertAndSend(
                ProductRabbitConfig.PRODUCT_EXCHANGE,
                ProductRabbitConfig.PRODUCT_UPDATED_ROUTING_KEY,
                new ProductUpdatedEvent(
                        updatedProduct.getId(),
                        updatedProduct.getTenantId(),
                        updatedProduct.getSku(),
                        updatedProduct.getName(),
                        Instant.now()
                )
        );

        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID productId) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Product product = productRepository
                .findByIdAndTenantIdAndDeletedFalse(
                        productId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found."
                        )
                );

        return productMapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(
            String search,
            UUID categoryId,
            UUID brandId,
            Boolean active,
            Pageable pageable
    ) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Page<Product> products = productRepository.searchProducts(
                tenantId,
                search,
                categoryId,
                brandId,
                active,
                pageable
        );

        return products.map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductBySku(String sku) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Product product = productRepository
                .findBySkuAndTenantIdAndDeletedFalse(
                        sku,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found."
                        )
                );

        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public void deactivateProduct(UUID productId) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Product product = productRepository
                .findByIdAndTenantIdAndDeletedFalse(
                        productId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found."
                        )
                );

        /*
         * TODO:
         * Check whether the product has active inventory,
         * sales, or purchase references before allowing
         * deactivation.
         */

        product.setActive(false);

        productRepository.save(product);
    }

    @Override
    @Transactional
    public void activateProduct(UUID productId) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Product product = productRepository
                .findByIdAndTenantIdAndDeletedFalse(
                        productId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found."
                        )
                );

        product.setActive(true);

        productRepository.save(product);
    }

    //Helper methods
    private void validateLeafCategory(Category category) {

        if (!category.isActive()) {
            throw new BusinessException(
                    "Category is inactive."
            );
        }

        boolean hasChildren = categoryRepository
                .existsByParentIdAndTenantIdAndDeletedFalse(
                        category.getId(),
                        category.getTenantId()
                );

        if (hasChildren) {
            throw new BusinessException(
                    "Category must be a leaf node."
            );
        }
    }

    private void validateSku(
            String sku,
            UUID tenantId
    ) {

        if (sku == null || sku.isBlank()) {
            return;
        }

        if (productRepository.existsByTenantIdAndSkuIgnoreCaseAndDeletedFalse(
                tenantId,
                sku
        )) {

            throw new DuplicateResourceException(
                    "Product SKU already exists."
            );
        }
    }

    private void validateBarcode(
            String barcode,
            UUID tenantId
    ) {

        if (barcode == null || barcode.isBlank()) {
            return;
        }

        if (productRepository.existsByTenantIdAndBarcodeAndDeletedFalse(
                tenantId,
                barcode
        )) {

            throw new DuplicateResourceException(
                    "Product barcode already exists."
            );
        }
    }

    private Brand getBrandEntity(
            UUID brandId,
            UUID tenantId
    ) {

        Brand brand = brandRepository
                .findByIdAndTenantIdAndDeletedFalse(
                        brandId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(

                                "Brand not found."
                        )
                );

        if (!brand.isActive()) {
            throw new BusinessException(
                    "Brand is inactive."
            );
        }

        return brand;
    }

    private TaxClass getTaxClassEntity(
            UUID taxClassId,
            UUID tenantId
    ) {

        return taxClassRepository
                .findByIdAndTenantIdAndDeletedFalse(
                        taxClassId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tax class not found."
                        ));
    }

    private String generateSku(
            Brand brand,
            Category category,
            UUID tenantId
    ) {

        String brandCode = brand != null
                ? brand.getCode()
                : "GEN";

        String categoryCode = category.getCode();

        long sequence = skuSequenceService.nextValue(tenantId);

        return "%s-%s-%06d".formatted(
                brandCode,
                categoryCode,
                sequence
        );
    }

    private Product getProductEntity(
            UUID productId,
            UUID tenantId
    ) {

        return productRepository
                .findByIdAndTenantIdAndDeletedFalse(
                        productId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found."
                        )
                );
    }

    private Category getCategoryEntity(
            UUID categoryId,
            UUID tenantId
    ) {

        return categoryRepository
                .findByIdAndTenantIdAndDeletedFalse(
                        categoryId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found."
                        ));
    }

    private void validateStockLevelRange(
            Integer minStockLevel,
            Integer maxStockLevel
    ) {
        if (minStockLevel == null || maxStockLevel == null) {
            return;
        }

        if (minStockLevel > maxStockLevel) {
            throw new BusinessException(
                    "Minimum stock level must not exceed maximum stock level."
            );
        }
    }
}
