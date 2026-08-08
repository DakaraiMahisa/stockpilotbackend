package com.stockpilot.backend.catalog.repository;

import com.stockpilot.backend.catalog.entity.Brand;
import com.stockpilot.backend.catalog.entity.Category;
import com.stockpilot.backend.catalog.entity.Product;
import com.stockpilot.backend.org.entity.TaxClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends
        JpaRepository<Product, UUID>,
        JpaSpecificationExecutor<Product> {


    Optional<Product> findByIdAndTenantIdAndDeletedFalse(
            UUID id,
            UUID tenantId
    );


    Optional<Product> findByTenantIdAndSkuAndDeletedFalse(
            UUID tenantId,
            String sku
    );

    Optional<Product> findByTenantIdAndBarcodeAndDeletedFalse(
            UUID tenantId,
            String barcode
    );

    boolean existsByTenantIdAndSkuIgnoreCaseAndDeletedFalse(
            UUID tenantId,
            String sku
    );

    boolean existsByTenantIdAndBarcodeAndDeletedFalse(
            UUID tenantId,
            String barcode
    );

    boolean existsByTenantIdAndSkuIgnoreCaseAndIdNotAndDeletedFalse(
            UUID tenantId,
            String sku,
            UUID id
    );

    boolean existsByTenantIdAndBarcodeAndIdNotAndDeletedFalse(
            UUID tenantId,
            String barcode,
            UUID id
    );



    Page<Product> findByTenantIdAndDeletedFalse(
            UUID tenantId,
            Pageable pageable
    );

    Page<Product> findByTenantIdAndActiveAndDeletedFalse(
            UUID tenantId,
            boolean active,
            Pageable pageable
    );

    Page<Product> findByTenantIdAndCategoryAndDeletedFalse(
            UUID tenantId,
            Category category,
            Pageable pageable
    );

    Page<Product> findByTenantIdAndBrandAndDeletedFalse(
            UUID tenantId,
            Brand brand,
            Pageable pageable
    );


    boolean existsByTenantIdAndCategoryAndDeletedFalse(
            UUID tenantId,
            Category category
    );

    boolean existsByTenantIdAndBrandAndDeletedFalse(
            UUID tenantId,
            Brand brand
    );

    boolean existsByTenantIdAndTaxClassAndDeletedFalse(
            UUID tenantId,
            TaxClass taxClass
    );


    @EntityGraph(attributePaths = {
            "brand",
            "category",
            "taxClass"
    })
    Optional<Product> findWithDetailsByIdAndTenantIdAndDeletedFalse(
            UUID id,
            UUID tenantId
    );
    long countByTenantIdAndDeletedFalse(UUID tenantId);

    long countByTenantId(UUID tenantId);

    Optional<Product> findBySkuAndTenantIdAndDeletedFalse(
            String sku,
            UUID tenantId
    );
    @Query(
            value = """
                SELECT p.*
                FROM products p
                WHERE p.tenant_id = :tenantId
                  AND p.deleted = FALSE

                  AND (
                        :active IS NULL
                        OR p.active = :active
                  )

                  AND (
                        :categoryId IS NULL
                        OR p.category_id = :categoryId
                  )

                  AND (
                        :brandId IS NULL
                        OR p.brand_id = :brandId
                  )

                  AND (
                        :search IS NULL
                        OR TRIM(:search) = ''
                        OR p.search_vector @@
                           websearch_to_tsquery('english', :search)
                  )
                """,
            countQuery = """
                SELECT COUNT(*)
                FROM products p
                WHERE p.tenant_id = :tenantId
                  AND p.deleted = FALSE

                  AND (
                        :active IS NULL
                        OR p.active = :active
                  )

                  AND (
                        :categoryId IS NULL
                        OR p.category_id = :categoryId
                  )

                  AND (
                        :brandId IS NULL
                        OR p.brand_id = :brandId
                  )

                  AND (
                        :search IS NULL
                        OR TRIM(:search) = ''
                        OR p.search_vector @@
                           websearch_to_tsquery('english', :search)
                  )
                """,
            nativeQuery = true
    )
    Page<Product> searchProducts(
            @Param("tenantId") UUID tenantId,
            @Param("search") String search,
            @Param("categoryId") UUID categoryId,
            @Param("brandId") UUID brandId,
            @Param("active") Boolean active,
            Pageable pageable
    );
}