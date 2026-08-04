package com.stockpilot.backend.catalog.entity;

import com.stockpilot.backend.shared.entity.TenantAwareEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "brands",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_brand_tenant_name",
                        columnNames = {"tenant_id", "name"}
                ),
                @UniqueConstraint(
                        name = "uk_brand_tenant_code",
                        columnNames = {"tenant_id", "code"}
                )
        }
)
@SQLRestriction("deleted = false")
public class Brand extends TenantAwareEntity {

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "^[A-Z0-9_-]+$")
    @Column(nullable = false, length = 20)
    private String code;

    @Size(max = 500)
    @Column(name = "logo_object_key", length = 500)
    private String logoObjectKey;

    @URL
    @Size(max = 200)
    @Column(length = 200)
    private String website;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}