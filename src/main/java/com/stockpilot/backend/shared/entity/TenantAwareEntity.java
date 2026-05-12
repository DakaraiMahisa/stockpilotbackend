package com.stockpilot.backend.shared.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class TenantAwareEntity extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private UUID tenantId;

    protected static <T extends TenantAwareEntity> T create(UUID tenantId, Class<T> entityClass) {
        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            entity.setTenantId(tenantId);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Reflection failed for " + entityClass.getName(), e);
        }
    }


    protected TenantAwareEntity(UUID tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TenantAwareEntity that = (TenantAwareEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}

