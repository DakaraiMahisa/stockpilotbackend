package com.stockpilot.backend.catalog.specifications;

import com.stockpilot.backend.catalog.entity.Brand;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class BrandSpecifications {

    private BrandSpecifications() {
    }

    public static Specification<Brand> hasTenant(UUID tenantId) {
        return (root, query, cb) ->
                cb.equal(root.get("tenantId"), tenantId);
    }

    public static Specification<Brand> hasActive(Boolean active) {

        if (active == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get("active"), active);
    }

    public static Specification<Brand> nameOrCodeContains(String search) {

        if (search == null || search.isBlank()) {
            return null;
        }

        String pattern = "%" + search.trim().toLowerCase() + "%";

        return (root, query, cb) ->
                cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("code")), pattern)
                );
    }
}