package com.stockpilot.backend.catalog.repository;

import com.stockpilot.backend.catalog.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
public interface ProductVariantRepository
        extends JpaRepository<ProductVariant, UUID> {

    Optional<ProductVariant>
    findByIdAndProductIdAndTenantIdAndDeletedFalse(
            UUID variantId,
            UUID productId,
            UUID tenantId
    );

    List<ProductVariant> findAllByProductIdAndTenantIdAndDeletedFalse(
            UUID productId,
            UUID tenantId
    );

    boolean existsByProductIdAndTenantIdAndSkuAndDeletedFalse(
            UUID productId,
            UUID tenantId,
            String sku
    );
    Optional<ProductVariant> findByIdAndTenantIdAndDeletedFalse(
            UUID variantId,
            UUID tenantId
    );

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM product_variants
                WHERE product_id = :productId
                  AND tenant_id = :tenantId
                  AND deleted = FALSE
                  AND attributes = CAST(:attributes AS jsonb)
            )
            """, nativeQuery = true)
    boolean existsByProductIdAndTenantIdAndAttributesAndDeletedFalse(
            @Param("productId") UUID productId,
            @Param("tenantId") UUID tenantId,
            @Param("attributes") String attributes
    );
}