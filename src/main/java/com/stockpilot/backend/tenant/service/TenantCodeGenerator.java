package com.stockpilot.backend.tenant.service;

import org.springframework.stereotype.Component;

@Component
public class TenantCodeGenerator {

    public String generate(String organizationName) {

        return organizationName
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}