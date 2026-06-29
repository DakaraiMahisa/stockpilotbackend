package com.stockpilot.backend.org.controller;

import com.stockpilot.backend.org.dto.*;
import com.stockpilot.backend.org.service.OrganizationService;
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
@RequestMapping(ApiRoutes.ORGANIZATIONS)
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<OrganizationDto>> getProfile() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        organizationService.getProfile(),
                        ApiMessages.ORG_PROFILE_RETRIEVED
                )
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<OrganizationDto>> updateProfile(
            @Valid @RequestBody OrganizationUpdateRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        organizationService.updateProfile(request),
                        ApiMessages.ORG_PROFILE_UPDATED
                )
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/logo/presigned")
    public ResponseEntity<ApiResponse<PresignedUploadResponse>> generatePresignedUrl(
            @Valid @RequestBody LogoPresignedRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        organizationService.generatePresignedUrl(request),
                        ApiMessages.LOGO_UPLOAD_URL_GENERATED
                )
        );
    }

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/logo/confirm")
    public ResponseEntity<ApiResponse<OrganizationDto>> confirmLogoUpload(
            @Valid @RequestBody LogoConfirmRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        organizationService.confirmLogoUpload(request),
                        ApiMessages.ORG_LOGO_UPDATED
                )
        );
    }
}
