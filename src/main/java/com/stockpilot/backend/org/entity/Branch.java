package com.stockpilot.backend.org.entity;

import com.stockpilot.backend.identity.domain.entity.User;
import com.stockpilot.backend.org.enums.BranchStatus;
import com.stockpilot.backend.org.enums.BranchType;
import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "branches",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_branch_tenant_name",
                        columnNames = {"tenant_id", "name"}
                ),
                @UniqueConstraint(
                        name = "uk_branch_tenant_code",
                        columnNames = {"tenant_id", "code"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Branch extends TenantAwareEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", nullable = false, length = 10)
    private String code;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "branch_type", nullable = false, length = 20)
    private BranchType branchType = BranchType.RETAIL;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "address_line_1", length = 200)
    private String addressLine1;

    @Column(name = "city", length = 100)
    private String city;

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    private boolean defaultBranch = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BranchStatus status = BranchStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private User manager;



    public boolean isActive() {
        return status == BranchStatus.ACTIVE;
    }

    public boolean isInactive() {
        return status == BranchStatus.INACTIVE;
    }

    public boolean isDraft() {
        return status == BranchStatus.DRAFT;
    }

    public boolean isArchived() {
        return status == BranchStatus.ARCHIVED;
    }

    public boolean hasManager() {
        return manager != null;
    }

    public String getDisplayName() {
        return code + " - " + name;
    }



    public void activate() {
        this.status = BranchStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = BranchStatus.INACTIVE;
    }

    public void archive() {
        this.status = BranchStatus.ARCHIVED;
    }

    public void markAsDefault() {
        this.defaultBranch = true;
    }

    public void removeAsDefault() {
        this.defaultBranch = false;
    }
}