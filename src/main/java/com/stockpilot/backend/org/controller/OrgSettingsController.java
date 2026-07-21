package com.stockpilot.backend.org.controller;

import com.stockpilot.backend.org.dto.request.UpdateGeneralSettingsRequest;
import com.stockpilot.backend.org.dto.request.UpdateInvitePolicyRequest;
import com.stockpilot.backend.org.dto.request.UpdatePasswordPolicyRequest;
import com.stockpilot.backend.org.dto.request.UpdateSessionPolicyRequest;
import com.stockpilot.backend.org.dto.response.OrgSettingsDto;
import com.stockpilot.backend.org.service.OrgSettingsService;
import com.stockpilot.backend.shared.api.ApiMessages;
import com.stockpilot.backend.shared.api.ApiResponse;
import com.stockpilot.backend.shared.api.ApiRoutes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiRoutes.ORG_SETTINGS)
@RequiredArgsConstructor
public class OrgSettingsController {

    private final OrgSettingsService orgSettingsService;

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.OrgSettingsPermissions).READ)")
    public ResponseEntity<ApiResponse<OrgSettingsDto>> getSettings() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        orgSettingsService.getSettings(),
                        ApiMessages.ORG_SETTINGS_RETRIEVED
                )
        );
    }

    @PutMapping("/password-policy")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.OrgSettingsPermissions).UPDATE_PASSWORD_POLICY)")
    public ResponseEntity<ApiResponse<OrgSettingsDto>> updatePasswordPolicy(
            @Valid @RequestBody UpdatePasswordPolicyRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        orgSettingsService.updatePasswordPolicy(request),
                        ApiMessages.PASSWORD_POLICY_UPDATED
                )
        );
    }

    @PutMapping("/session-policy")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.OrgSettingsPermissions).UPDATE_SESSION_POLICY)")
    public ResponseEntity<ApiResponse<OrgSettingsDto>> updateSessionPolicy(
            @Valid @RequestBody UpdateSessionPolicyRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        orgSettingsService.updateSessionPolicy(request),
                        ApiMessages.SESSION_POLICY_UPDATED
                )
        );
    }

    @PutMapping("/invite-policy")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.OrgSettingsPermissions).UPDATE_INVITE_POLICY)")
    public ResponseEntity<ApiResponse<OrgSettingsDto>> updateInvitePolicy(
            @Valid @RequestBody UpdateInvitePolicyRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        orgSettingsService.updateInvitePolicy(request),
                        ApiMessages.INVITE_POLICY_UPDATED
                )
        );
    }

    @PutMapping("/general")
    @PreAuthorize("hasAuthority(T(com.stockpilot.backend.shared.security.permissions.OrgSettingsPermissions).UPDATE_GENERAL)")
    public ResponseEntity<ApiResponse<OrgSettingsDto>> updateGeneralSettings(
            @Valid @RequestBody UpdateGeneralSettingsRequest request
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        orgSettingsService.updateGeneralSettings(request),
                        ApiMessages.GENERAL_SETTINGS_UPDATED
                )
        );
    }
}