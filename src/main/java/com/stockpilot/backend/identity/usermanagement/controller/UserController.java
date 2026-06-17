package com.stockpilot.backend.identity.usermanagement.controller;

import com.stockpilot.backend.identity.usermanagement.dto.UserDetailsDto;
import com.stockpilot.backend.identity.usermanagement.dto.UserSessionDto;
import com.stockpilot.backend.identity.usermanagement.dto.UserSummaryDto;
import com.stockpilot.backend.identity.usermanagement.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
    public Page<UserSummaryDto> listUsers(

            @RequestParam(required = false)
            UUID roleId,

            @RequestParam(required = false)
            Boolean active,

            @PageableDefault(size = 20)
            Pageable pageable
    ) {

        return userManagementService.listUsers(
                roleId,
                active,
                pageable
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public UserDetailsDto getUser(
            @PathVariable UUID id
    ) {
        return userManagementService.getUser(id);
    }

    @GetMapping("/{id}/sessions")
    @PreAuthorize("hasRole('OWNER')")
    public List<UserSessionDto> getUserSessions(
            @PathVariable UUID id
    ) {
        return userManagementService.getUserSessions(id);
    }

    @DeleteMapping("/{id}/sessions/{sid}")
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeSession(
            @PathVariable UUID id,
            @PathVariable("sid") UUID sessionId
    ) {
        userManagementService.revokeSession(id, sessionId);
    }


    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateUser(@PathVariable UUID id) {
        userManagementService.deactivateUser(id);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateUser(
            @PathVariable UUID id
    ) {
        userManagementService.activateUser(id);
    }
}