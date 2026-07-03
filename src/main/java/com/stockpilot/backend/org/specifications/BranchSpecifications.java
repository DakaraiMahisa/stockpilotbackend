package com.stockpilot.backend.org.specifications;


import com.stockpilot.backend.org.entity.Branch;
import com.stockpilot.backend.org.enums.BranchStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class BranchSpecifications {

    private BranchSpecifications() {
    }

    public static Specification<Branch> belongsToTenant(UUID tenantId) {
        return (root, query, cb) ->
                cb.equal(root.get("tenantId"), tenantId);
    }

    public static Specification<Branch> hasStatus(BranchStatus status) {
        return (root, query, cb) ->
                status == null
                        ? cb.conjunction()
                        : cb.equal(root.get("status"), status);
    }

    public static Specification<Branch> notDeleted() {
        return (root, query, cb) ->
                cb.isFalse(root.get("deleted"));
    }
}