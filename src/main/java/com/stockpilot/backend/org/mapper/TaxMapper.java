package com.stockpilot.backend.org.mapper;

import com.stockpilot.backend.org.dto.request.AddTaxRateRequest;
import com.stockpilot.backend.org.dto.request.CreateTaxClassRequest;
import com.stockpilot.backend.org.dto.request.CreateTaxRateRequest;
import com.stockpilot.backend.org.dto.request.UpdateTaxClassRequest;
import com.stockpilot.backend.org.dto.response.TaxClassDto;
import com.stockpilot.backend.org.dto.response.TaxRateDto;
import com.stockpilot.backend.org.entity.TaxClass;
import com.stockpilot.backend.org.entity.TaxRate;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TaxMapper {



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "rates", ignore = true)
    @Mapping(target = "defaultTaxClass", source = "isDefault")
    TaxClass toEntity(CreateTaxClassRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "taxClass", ignore = true)
    @Mapping(target = "effectiveTo", ignore = true)
    TaxRate toEntity(CreateTaxRateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "taxClass", ignore = true)
    @Mapping(target = "effectiveTo", ignore = true)
    TaxRate toEntity(AddTaxRateRequest request);


    TaxClassDto toDto(TaxClass taxClass);

    TaxRateDto toDto(TaxRate taxRate);


    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "taxType", ignore = true)
    @Mapping(target = "defaultTaxClass", ignore = true)
    @Mapping(target = "rates", ignore = true)
    void updateEntityFromRequest(
            UpdateTaxClassRequest request,
            @MappingTarget TaxClass taxClass
    );
}