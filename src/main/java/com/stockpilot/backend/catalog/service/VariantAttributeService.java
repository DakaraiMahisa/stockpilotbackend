package com.stockpilot.backend.catalog.service;


import com.stockpilot.backend.catalog.dto.request.CreateVariantAttributeRequest;
import com.stockpilot.backend.catalog.dto.request.CreateVariantAttributeValueRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateVariantAttributeRequest;
import com.stockpilot.backend.catalog.dto.request.UpdateVariantAttributeValueRequest;
import com.stockpilot.backend.catalog.dto.response.VariantAttributeDto;
import com.stockpilot.backend.catalog.dto.response.VariantAttributeValueDto;

import java.util.List;
import java.util.UUID;

public interface VariantAttributeService {

    // -------------------------------------------------------------------------
    // Variant Attributes
    // -------------------------------------------------------------------------

    VariantAttributeDto createAttribute(
            CreateVariantAttributeRequest request
    );

    List<VariantAttributeDto> getAllAttributes();

    VariantAttributeDto getAttributeById(
            UUID attributeId
    );

    VariantAttributeDto updateAttribute(
            UUID attributeId,
            UpdateVariantAttributeRequest request
    );

    void activateAttribute(
            UUID attributeId
    );

    void deactivateAttribute(
            UUID attributeId
    );

    void deleteAttribute(
            UUID attributeId
    );

    // -------------------------------------------------------------------------
    // Variant Attribute Values
    // -------------------------------------------------------------------------

    VariantAttributeValueDto createValue(
            UUID attributeId,
            CreateVariantAttributeValueRequest request
    );

    List<VariantAttributeValueDto> getValues(
            UUID attributeId
    );

    VariantAttributeValueDto getValueById(
            UUID attributeId,
            UUID valueId
    );

    VariantAttributeValueDto updateValue(
            UUID attributeId,
            UUID valueId,
            UpdateVariantAttributeValueRequest request
    );

    void activateValue(
            UUID attributeId,
            UUID valueId
    );

    void deactivateValue(
            UUID attributeId,
            UUID valueId
    );

    void deleteValue(
            UUID attributeId,
            UUID valueId
    );
}