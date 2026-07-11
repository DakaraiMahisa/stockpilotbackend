package com.stockpilot.backend.identity.rolemanagement.controller;

import com.stockpilot.backend.identity.rolemanagement.dto.RoleSummaryDto;
import com.stockpilot.backend.identity.rolemanagement.service.RoleService;
import com.stockpilot.backend.shared.api.ApiRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiRoutes.ROLES)
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PreAuthorize(
            "hasAuthority(T(com.stockpilot.backend.shared.security.permissions.RolePermissions).READ)"
    )
    @GetMapping
    public List<RoleSummaryDto> listRoles() {
        return roleService.listRoles();
    }
}
