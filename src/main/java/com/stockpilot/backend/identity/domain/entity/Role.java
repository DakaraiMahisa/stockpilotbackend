package com.stockpilot.backend.identity.domain.entity;

import com.stockpilot.backend.identity.domain.enums.RoleName;
import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_role_name_tenant",
                        columnNames = {"name", "tenant_id"}
                )
        },
        indexes = {
                @Index(name = "idx_role_tenant_id", columnList = "tenant_id")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends TenantAwareEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 50, nullable = false)
    private RoleName name;

    @ManyToMany
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "permission_id", nullable = false)
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();
}

