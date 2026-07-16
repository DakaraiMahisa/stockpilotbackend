package com.stockpilot.backend.org.service;

import com.stockpilot.backend.org.dto.request.CreateTaxClassRequest;
import com.stockpilot.backend.org.dto.request.CreateTaxRateRequest;
import com.stockpilot.backend.org.dto.request.UpdateTaxClassRequest;
import com.stockpilot.backend.org.dto.response.TaxBreakdownDto;
import com.stockpilot.backend.org.dto.response.TaxClassDto;
import com.stockpilot.backend.org.dto.response.TaxRateDto;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaxService {

    List<TaxClassDto> getTaxClasses(boolean activeOnly);
    TaxClassDto getTaxClass(UUID id);

    TaxClassDto createTaxClass(CreateTaxClassRequest request);

    TaxClassDto updateTaxClass(
            UUID taxClassId,
            UpdateTaxClassRequest request
    );

    TaxRateDto addTaxRate(
            UUID taxClassId,
            @Valid CreateTaxRateRequest request
    );

    TaxClassDto setDefaultTaxClass(UUID taxClassId);

    TaxBreakdownDto resolveTax(
            UUID taxClassId,
            BigDecimal amount,
            LocalDate transactionDate
    );
}