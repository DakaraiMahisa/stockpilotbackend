package com.stockpilot.backend.identity.usermanagement.specifications;

import com.stockpilot.backend.identity.domain.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<User> belongsToTenant(UUID tenantId) {
        return (root, query, cb) ->
                cb.equal(root.get("tenantId"), tenantId);
    }

    public static Specification<User> hasRole(UUID roleId) {
        return (root, query, cb) ->
                roleId == null
                        ? cb.conjunction()
                        : cb.equal(root.get("role").get("id"), roleId);
    }

    public static Specification<User> isActive(Boolean active) {
        return (root, query, cb) ->
                active == null
                        ? cb.conjunction()
                        : cb.equal(root.get("active"), active);
    }

    public static Specification<User> notDeleted() {
        return (root, query, cb) ->
                cb.isFalse(root.get("deleted"));
    }
}