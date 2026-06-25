package com.stockpilot.backend.identity.rolemanagement.controller;

import com.stockpilot.backend.identity.rolemanagement.dto.RoleSummaryDto;
import com.stockpilot.backend.identity.rolemanagement.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
    public List<RoleSummaryDto> listRoles() {
        return roleService.listRoles();
    }
}
