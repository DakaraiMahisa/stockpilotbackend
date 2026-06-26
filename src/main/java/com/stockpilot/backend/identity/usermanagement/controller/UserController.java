package com.stockpilot.backend.identity.usermanagement.controller;

import com.stockpilot.backend.identity.usermanagement.dto.*;
import com.stockpilot.backend.identity.usermanagement.service.UserManagementService;
import com.stockpilot.backend.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserManagementService userManagementService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse<Page<UserSummaryDto>>> listUsers(

            @RequestParam(required = false)
            UUID roleId,

            @RequestParam(required = false)
            Boolean active,

            @PageableDefault(size = 20)
            Pageable pageable
    ) {

        Page<UserSummaryDto> users = userManagementService.listUsers(
                roleId,
                active,
                pageable
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        users,
                        "Users retrieved successfully."
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public ResponseEntity<ApiResponse< UserDetailsDto>> getUser(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        userManagementService.getUser(id),
                        "User retrieved successfully."));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDetailsDto>> getCurrentUser() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        userManagementService.getCurrentUser(),
                        "Current user retrieved successfully."));
    }

    @GetMapping("/{id}/sessions")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<List<UserSessionDto>>> getUserSessions(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        userManagementService.getUserSessions(id),
                        "User sessions retrieved successfully."
                )
        );
    }

    @DeleteMapping("/{id}/sessions/{sid}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Void>> revokeSession(
            @PathVariable UUID id,
            @PathVariable("sid") UUID sessionId
    ) {
        userManagementService.revokeSession(id, sessionId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Session revoked successfully."
                )
        );
    }


    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable UUID id) {
        userManagementService.deactivateUser(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "User deactivated successfully."
                )
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Void>> activateUser(
            @PathVariable UUID id
    ) {

        userManagementService.activateUser(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "User activated successfully."
                )
        );
    }

    @PostMapping("/invite")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Void>> inviteUser(
            @Valid @RequestBody InviteUserRequestDto request
    ) {

        userManagementService.inviteUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                null,
                                "Invitation sent successfully."
                        )
                );
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<Void>> changeUserRole(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeUserRoleRequestDto request
    ) {
        userManagementService.changeUserRole(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "User role changed successfully."
                )
        );
    }
}