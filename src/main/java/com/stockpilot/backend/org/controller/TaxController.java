package com.stockpilot.backend.org.controller;

import com.stockpilot.backend.org.dto.request.CreateTaxClassRequest;
import com.stockpilot.backend.org.dto.request.CreateTaxRateRequest;
import com.stockpilot.backend.org.dto.request.UpdateTaxClassRequest;
import com.stockpilot.backend.org.dto.response.TaxBreakdownDto;
import com.stockpilot.backend.org.dto.response.TaxClassDto;
import com.stockpilot.backend.org.dto.response.TaxRateDto;
import com.stockpilot.backend.org.service.TaxService;
import com.stockpilot.backend.shared.api.ApiMessages;
import com.stockpilot.backend.shared.api.ApiResponse;
import com.stockpilot.backend.shared.api.ApiRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.TAX)
@RequiredArgsConstructor
@Validated
public class TaxController {

    private final TaxService taxService;

    @GetMapping("/classes")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.TaxConfigPermission).READ)")
    public ResponseEntity<ApiResponse<List<TaxClassDto>>> getTaxClasses(
            @RequestParam(defaultValue = "false") boolean activeOnly
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        taxService.getTaxClasses(activeOnly),
                        ApiMessages.TAX_CLASSES_RETRIEVED
                )
        );
    }

    @GetMapping("/classes/{id}")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.TaxConfigPermission).READ)")
    public ResponseEntity<ApiResponse<TaxClassDto>> getTaxClass(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        taxService.getTaxClass(id),
                        ApiMessages.TAX_CLASS_RETRIEVED
                )
        );
    }

    @PostMapping("/classes")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.TaxConfigPermission).CREATE)")
    public ResponseEntity<ApiResponse<TaxClassDto>> createTaxClass(
            @Valid @RequestBody CreateTaxClassRequest request
    ) {

        TaxClassDto taxClass =
                taxService.createTaxClass(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        taxClass,
                        ApiMessages.TAX_CLASS_CREATED
                ));
    }

    @PutMapping("/classes/{id}")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.TaxConfigPermission).UPDATE)")
    public ResponseEntity<ApiResponse<TaxClassDto>> updateTaxClass(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaxClassRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        taxService.updateTaxClass(id, request),
                        ApiMessages.TAX_CLASS_UPDATED
                )
        );
    }

    @PostMapping("/classes/{id}/rates")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.TaxConfigPermission).UPDATE)")
    public ResponseEntity<ApiResponse<TaxRateDto>> addTaxRate(
            @PathVariable UUID id,
            @Valid @RequestBody CreateTaxRateRequest request
    ) {

        TaxRateDto rate =
                taxService.addTaxRate(id, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        rate,
                        ApiMessages.TAX_RATE_CREATED
                ));
    }

    @PatchMapping("/classes/{id}/default")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.TaxConfigPermission).SET_DEFAULT)")
    public ResponseEntity<ApiResponse<TaxClassDto>> setDefaultTaxClass(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        taxService.setDefaultTaxClass(id),
                        ApiMessages.DEFAULT_TAX_CLASS_UPDATED
                )
        );
    }

    @GetMapping("/resolve")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.TaxConfigPermission).RESOLVE)")
    public ResponseEntity<ApiResponse<TaxBreakdownDto>> resolveTax(
            @RequestParam UUID taxClassId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate transactionDate
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        taxService.resolveTax(
                                taxClassId,
                                amount,
                                transactionDate != null
                                        ? transactionDate
                                        : LocalDate.now()
                        ),
                        ApiMessages.TAX_RESOLVED
                )
        );
    }
}
