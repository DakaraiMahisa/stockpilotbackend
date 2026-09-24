package com.stockpilot.backend.catalog.service.impl;

import com.stockpilot.backend.catalog.dto.request.CreateVariantAttributeRequest;
import com.stockpilot.backend.catalog.dto.request.CreateVariantAttributeValueRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateVariantAttributeRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateVariantAttributeValueRequest;
import com.stockpilot.backend.catalog.dto.response.VariantAttributeDto;
import com.stockpilot.backend.catalog.dto.response.VariantAttributeValueDto;
import com.stockpilot.backend.catalog.entity.VariantAttribute;
import com.stockpilot.backend.catalog.entity.VariantAttributeValue;
import com.stockpilot.backend.catalog.repository.ProductVariantAttributeRepository;
import com.stockpilot.backend.catalog.repository.VariantAttributeRepository;
import com.stockpilot.backend.catalog.repository.VariantAttributeValueRepository;
import com.stockpilot.backend.catalog.service.VariantAttributeService;
import com.stockpilot.backend.shared.exception.base.BusinessRuleException;
import com.stockpilot.backend.shared.exception.base.ResourceNotFoundException;
import com.stockpilot.backend.shared.utils.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VariantAttributeServiceImpl
        implements VariantAttributeService{

    private final VariantAttributeRepository variantAttributeRepository;
    private final VariantAttributeValueRepository variantAttributeValueRepository;
    private final ProductVariantAttributeRepository productVariantAttributeRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;


    @Override
    @Transactional
    public VariantAttributeDto createAttribute(
            CreateVariantAttributeRequest request
    ) {
        UUID tenantId = getTenantId();

        String code = request.code().trim();

        if (variantAttributeRepository.existsByCodeAndTenantId(code, tenantId)) {
            throw new BusinessRuleException(
                    "Variant attribute with code '" + code + "' already exists"
            );
        }

        VariantAttribute attribute = VariantAttribute.builder()
                .tenantId(tenantId)
                .name(request.name().trim())
                .code(code)
                .description(
                        request.description() == null
                                ? null
                                : request.description().trim()
                )
                .active(true)
                .build();

        VariantAttribute savedAttribute =
                variantAttributeRepository.save(attribute);

        return toDto(savedAttribute);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VariantAttributeDto> getAllAttributes() {
        UUID tenantId = getTenantId();

        return variantAttributeRepository
                .findAllByTenantIdOrderByNameAsc(tenantId)
                .stream()
                .map(this::toDto)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public VariantAttributeDto getAttributeById(
            UUID attributeId
    ) {
        UUID tenantId = getTenantId();

        VariantAttribute attribute =
                variantAttributeRepository
                        .findByIdAndTenantId(attributeId, tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant attribute not found"
                                )
                        );

        return toDto(attribute);
    }

    @Override
    @Transactional
    public VariantAttributeDto updateAttribute(
            UUID attributeId,
            UpdateVariantAttributeRequest request
    ) {
        UUID tenantId = getTenantId();

        VariantAttribute attribute =
                variantAttributeRepository
                        .findByIdAndTenantId(attributeId, tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant attribute not found"
                                )
                        );

        String code = request.code().trim();

        if (!attribute.getCode().equals(code)
                && variantAttributeRepository.existsByCodeAndTenantId(
                code,
                tenantId
        )) {
            throw new BusinessRuleException(
                    "Variant attribute with code '" + code + "' already exists"
            );
        }

        attribute.setName(request.name().trim());
        attribute.setCode(code);
        attribute.setDescription(
                request.description() == null
                        ? null
                        : request.description().trim()
        );

        VariantAttribute savedAttribute =
                variantAttributeRepository.save(attribute);

        return toDto(savedAttribute);
    }

    @Override
    @Transactional
    public void activateAttribute(UUID attributeId) {
        UUID tenantId = getTenantId();

        VariantAttribute attribute =
                variantAttributeRepository
                        .findByIdAndTenantId(attributeId, tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant attribute not found"
                                )
                        );

        if (attribute.isActive()) {
            throw new BusinessRuleException(
                    "Variant attribute is already active"
            );
        }

        attribute.setActive(true);

        variantAttributeRepository.save(attribute);
    }

    @Override
    @Transactional
    public void deactivateAttribute(UUID attributeId) {
        UUID tenantId = getTenantId();

        VariantAttribute attribute =
                variantAttributeRepository
                        .findByIdAndTenantId(attributeId, tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant attribute not found"
                                )
                        );

        if (!attribute.isActive()) {
            throw new BusinessRuleException(
                    "Variant attribute is already inactive"
            );
        }

        attribute.setActive(false);

        variantAttributeRepository.save(attribute);
    }

    @Override
    @Transactional
    public void deleteAttribute(UUID attributeId) {
        UUID tenantId = getTenantId();

        VariantAttribute attribute =
                variantAttributeRepository
                        .findByIdAndTenantId(attributeId, tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant attribute not found"
                                )
                        );

        if (productVariantAttributeRepository
                .existsByAttributeIdAndTenantId(attributeId, tenantId)) {
            throw new BusinessRuleException(
                    "Variant attribute cannot be deleted because it is assigned to one or more products"
            );
        }

        attribute.setDeleted(true);

        variantAttributeRepository.save(attribute);
    }

    @Override
    @Transactional
    public VariantAttributeValueDto createValue(
            UUID attributeId,
            CreateVariantAttributeValueRequest request
    ){
        UUID tenantId = getTenantId();

        VariantAttribute attribute =
                variantAttributeRepository
                        .findByIdAndTenantId(attributeId, tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant attribute not found"
                                )
                        );

        if (!attribute.isActive()) {
            throw new BusinessRuleException(
                    "Cannot add a value to an inactive variant attribute"
            );
        }

        String code = request.code().trim();

        if (variantAttributeValueRepository
                .existsByAttributeIdAndCodeAndTenantId(
                        attributeId,
                        code,
                        tenantId
                )) {
            throw new BusinessRuleException(
                    "Variant attribute value with code '" + code
                            + "' already exists"
            );
        }

        VariantAttributeValue value =
                VariantAttributeValue.builder()
                        .tenantId(tenantId)
                        .attribute(attribute)
                        .value(request.value().trim())
                        .code(code)
                        .active(true)
                        .sortOrder(request.sortOrder())
                        .build();

        VariantAttributeValue savedValue =
                variantAttributeValueRepository.save(value);

        return toValueDto(savedValue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VariantAttributeValueDto> getValues(
            UUID attributeId
    ) {
        UUID tenantId = getTenantId();

        variantAttributeRepository
                .findByIdAndTenantId(attributeId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Variant attribute not found"
                        )
                );

        return variantAttributeValueRepository
                .findAllByAttributeIdAndTenantIdAndActiveTrueOrderBySortOrderAsc(
                        attributeId,
                        tenantId
                )
                .stream()
                .map(this::toValueDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VariantAttributeValueDto getValueById(
            UUID attributeId,
            UUID valueId
    ) {
        UUID tenantId = getTenantId();

        variantAttributeRepository
                .findByIdAndTenantId(attributeId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Variant attribute not found"
                        )
                );

        VariantAttributeValue value =
                variantAttributeValueRepository
                        .findByIdAndTenantId(valueId, tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant attribute value not found"
                                )
                        );

        if (!value.getAttribute().getId().equals(attributeId)) {
            throw new ResourceNotFoundException(
                    "Variant attribute value not found"
            );
        }

        return toValueDto(value);
    }

    @Override
    @Transactional
    public VariantAttributeValueDto updateValue(
            UUID attributeId,
            UUID valueId,
            UpdateVariantAttributeValueRequest request
    ){
        UUID tenantId = getTenantId();

        VariantAttribute attribute =
                variantAttributeRepository
                        .findByIdAndTenantId(attributeId, tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant attribute not found"
                                )
                        );

        if (!attribute.isActive()) {
            throw new BusinessRuleException(
                    "Cannot update a value of an inactive variant attribute"
            );
        }

        VariantAttributeValue value =
                variantAttributeValueRepository
                        .findByIdAndTenantId(valueId, tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant attribute value not found"
                                )
                        );

        if (!value.getAttribute().getId().equals(attributeId)) {
            throw new ResourceNotFoundException(
                    "Variant attribute value not found"
            );
        }

        String code = request.code().trim();

        if (!value.getCode().equals(code)
                && variantAttributeValueRepository
                .existsByAttributeIdAndCodeAndTenantId(
                        attributeId,
                        code,
                        tenantId
                )) {
            throw new BusinessRuleException(
                    "Variant attribute value with code '" + code
                            + "' already exists"
            );
        }

        value.setValue(request.value().trim());
        value.setCode(code);
        value.setSortOrder(request.sortOrder());

        VariantAttributeValue savedValue =
                variantAttributeValueRepository.save(value);

        return toValueDto(savedValue);
    }

    @Override
    @Transactional
    public void activateValue(
            UUID attributeId,
            UUID valueId
    ) {
        UUID tenantId = getTenantId();

        VariantAttribute attribute =
                variantAttributeRepository
                        .findByIdAndTenantId(attributeId, tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant attribute not found"
                                )
                        );

        if (!attribute.isActive()) {
            throw new BusinessRuleException(
                    "Cannot activate a value under an inactive variant attribute"
            );
        }

        VariantAttributeValue value =
                variantAttributeValueRepository
                        .findByIdAndTenantId(valueId, tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant attribute value not found"
                                )
                        );

        if (!value.getAttribute().getId().equals(attributeId)) {
            throw new ResourceNotFoundException(
                    "Variant attribute value not found"
            );
        }

        if (value.isActive()) {
            throw new BusinessRuleException(
                    "Variant attribute value is already active"
            );
        }

        value.setActive(true);

        variantAttributeValueRepository.save(value);
    }

    @Override
    @Transactional
    public void deactivateValue(
            UUID attributeId,
            UUID valueId
    ) {
        UUID tenantId = getTenantId();

        VariantAttribute attribute =
                variantAttributeRepository
                        .findByIdAndTenantId(attributeId, tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant attribute not found"
                                )
                        );

        VariantAttributeValue value =
                variantAttributeValueRepository
                        .findByIdAndTenantId(valueId, tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant attribute value not found"
                                )
                        );

        if (!value.getAttribute().getId().equals(attributeId)) {
            throw new ResourceNotFoundException(
                    "Variant attribute value not found"
            );
        }

        if (!value.isActive()) {
            throw new BusinessRuleException(
                    "Variant attribute value is already inactive"
            );
        }

        value.setActive(false);

        variantAttributeValueRepository.save(value);
    }

    @Override
    @Transactional
    public void deleteValue(
            UUID attributeId,
            UUID valueId
    ) {
        UUID tenantId = getTenantId();

        variantAttributeRepository
                .findByIdAndTenantId(attributeId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Variant attribute not found"
                        )
                );

        VariantAttributeValue value =
                variantAttributeValueRepository
                        .findByIdAndTenantId(valueId, tenantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Variant attribute value not found"
                                )
                        );

        if (!value.getAttribute().getId().equals(attributeId)) {
            throw new ResourceNotFoundException(
                    "Variant attribute value not found"
            );
        }

        value.setDeleted(true);

        variantAttributeValueRepository.save(value);
    }
    //HELPER METHODS

    private VariantAttributeDto toDto(
            VariantAttribute attribute
    ) {
        UUID tenantId = getTenantId();

        List<VariantAttributeValueDto> values =
                variantAttributeValueRepository
                        .findAllByAttributeIdAndTenantIdAndActiveTrueOrderBySortOrderAsc(
                                attribute.getId(),
                                tenantId
                        )
                        .stream()
                        .map(this::toValueDto)
                        .toList();

        return VariantAttributeDto.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .code(attribute.getCode())
                .description(attribute.getDescription())
                .active(attribute.isActive())
                .values(values)
                .build();
    }

    private VariantAttributeValueDto toValueDto(
            VariantAttributeValue value
    ) {
        return VariantAttributeValueDto.builder()
                .id(value.getId())
                .attributeId(value.getAttribute().getId())
                .value(value.getValue())
                .code(value.getCode())
                .active(value.isActive())
                .sortOrder(value.getSortOrder())
                .build();
    }

    private UUID getTenantId() {
        return authenticatedUserProvider
                .getCurrentUser()
                .getTenantId();
    }
}
