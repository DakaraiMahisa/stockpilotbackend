package com.stockpilot.backend.org.controller;

import com.stockpilot.backend.org.dto.request.BusinessConfigUpdateRequest;
import com.stockpilot.backend.org.dto.response.BusinessConfigDto;
import com.stockpilot.backend.org.service.BusinessConfigService;
import com.stockpilot.backend.shared.api.ApiMessages;
import com.stockpilot.backend.shared.api.ApiResponse;
import com.stockpilot.backend.shared.api.ApiRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.BUSINESS_CONFIG)
public class BusinessConfigController {

    private final BusinessConfigService businessConfigService;

    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.BusinessConfigPermissions).READ)")
    @GetMapping
    public ResponseEntity<ApiResponse<BusinessConfigDto>> getConfiguration() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        businessConfigService.getConfiguration(),
                        ApiMessages.BUSINESS_CONFIG_RETRIEVED
                )
        );
    }

    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.BusinessConfigPermissions).UPDATE)")
    @PutMapping
    public ResponseEntity<ApiResponse<BusinessConfigDto>> updateConfiguration(
            @Valid @RequestBody BusinessConfigUpdateRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        businessConfigService.updateConfiguration(request),
                        ApiMessages.BUSINESS_CONFIG_UPDATED
                )
        );
    }
}