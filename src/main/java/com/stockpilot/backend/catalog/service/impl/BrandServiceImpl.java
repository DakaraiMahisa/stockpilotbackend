package com.stockpilot.backend.catalog.service.impl;

import com.stockpilot.backend.catalog.dto.request.BrandCreateRequest;
import com.stockpilot.backend.catalog.dto.request.BrandUpdateRequest;
import com.stockpilot.backend.catalog.dto.response.BrandDto;
import com.stockpilot.backend.catalog.dto.response.BrandSummaryDto;
import com.stockpilot.backend.catalog.entity.Brand;
import com.stockpilot.backend.catalog.mapper.BrandMapper;
import com.stockpilot.backend.catalog.repository.BrandRepository;
import com.stockpilot.backend.catalog.service.BrandService;
import com.stockpilot.backend.catalog.specifications.BrandSpecifications;
import com.stockpilot.backend.shared.exception.base.DuplicateResourceException;
import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    @Transactional
    public BrandDto createBrand(BrandCreateRequest request) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        String normalizedName = request.name().trim();
        String normalizedCode = request.code().trim().toUpperCase(Locale.ROOT);

        validateBrandNameUniqueness(tenantId, normalizedName);
        validateBrandCodeUniqueness(tenantId, normalizedCode);

        Brand brand = brandMapper.toEntity(request);

        brand.setTenantId(tenantId);
        brand.setName(normalizedName);
        brand.setCode(normalizedCode);

        Brand savedBrand = brandRepository.save(brand);

        return brandMapper.toDto(savedBrand);
    }

    @Override
    @Transactional
    public BrandDto updateBrand(UUID brandId, BrandUpdateRequest request) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Brand brand = brandRepository.findByIdAndTenantId(brandId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Brand not found with id: " + brandId
                ));

        if (request.name() != null) {

            String normalizedName = request.name().trim();

            if (brandRepository.existsByTenantIdAndNameIgnoreCaseAndIdNot(
                    tenantId,
                    normalizedName,
                    brandId
            )) {
                throw new DuplicateResourceException(
                        "Brand with name '%s' already exists."
                                .formatted(normalizedName)
                );
            }
        }

        if (request.code() != null) {

            String normalizedCode = request.code().trim().toUpperCase(Locale.ROOT);

            if (brandRepository.existsByTenantIdAndCodeIgnoreCaseAndIdNot(
                    tenantId,
                    normalizedCode,
                    brandId
            )) {
                throw new DuplicateResourceException(
                        "Brand with code '%s' already exists."
                                .formatted(normalizedCode)
                );
            }

            // TODO:
            // Once the Product module is implemented, prevent changing the code
            // if products already reference this brand.

            brand.setCode(normalizedCode);
        }

        brandMapper.updateEntityFromRequest(request, brand);

        if (request.name() != null) {
            brand.setName(request.name().trim());
        }

        Brand updatedBrand = brandRepository.save(brand);

        return brandMapper.toDto(updatedBrand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDto getBrandById(UUID brandId) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Brand brand = brandRepository.findByIdAndTenantId(brandId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Brand not found with id: " + brandId
                ));

        return brandMapper.toDto(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandDto> getBrands(
            String search,
            Boolean active,
            Pageable pageable
    ) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Specification<Brand> specification =
                BrandSpecifications.hasTenant(tenantId);

        if (search != null && !search.isBlank()) {
            specification = specification.and(
                    BrandSpecifications.nameOrCodeContains(search.trim())
            );
        }

        if (active != null) {
            specification = specification.and(
                    BrandSpecifications.hasActive(active)
            );
        }

        return brandRepository.findAll(specification, pageable)
                .map(brandMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandSummaryDto> getActiveBrands() {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        return brandRepository.findAllByTenantIdAndActiveTrueOrderByNameAsc(tenantId)
                .stream()
                .map(brandMapper::toSummaryDto)
                .toList();
    }

    @Override
    @Transactional
    public void deactivateBrand(UUID brandId) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Brand brand = getBrandEntity(brandId, tenantId);

        // TODO:
        // Validate no active products reference this brand.

        brand.setActive(false);

        brandRepository.save(brand);
    }

    @Override
    @Transactional
    public BrandDto activateBrand(UUID brandId) {

        UUID tenantId = authenticatedUserProvider.getCurrentTenantId();

        Brand brand = getBrandEntity(brandId, tenantId);

        brand.setActive(true);

        Brand activatedBrand = brandRepository.save(brand);

        return brandMapper.toDto(activatedBrand);
    }

    // Helper methods

    private void validateBrandNameUniqueness(UUID tenantId, String name) {

             if (brandRepository.existsByTenantIdAndNameIgnoreCase(tenantId, name)) {
                 throw new DuplicateResourceException(
                         "Brand with name '%s' already exists.".formatted(name)
                 );
             }
         }

    private void validateBrandCodeUniqueness(UUID tenantId, String code) {

        if (brandRepository.existsByTenantIdAndCodeIgnoreCase(tenantId, code)) {
            throw new DuplicateResourceException(
                    "Brand with code '%s' already exists.".formatted(code)
            );
        }
    }

    private Brand getBrandEntity(UUID brandId, UUID tenantId) {

        return brandRepository.findByIdAndTenantId(brandId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Brand not found with id: " + brandId
                ));
    }
}
