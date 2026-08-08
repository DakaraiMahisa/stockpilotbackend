package com.stockpilot.backend.catalog.repository;

import com.stockpilot.backend.catalog.entity.SkuSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SkuSequenceRepository extends JpaRepository<SkuSequence, UUID> {

    @Query(value = """
        INSERT INTO sku_sequences (tenant_id, next_value)
        VALUES (:tenantId, 2)
        ON CONFLICT (tenant_id)
        DO UPDATE
        SET next_value = sku_sequences.next_value + 1
        RETURNING next_value - 1
        """, nativeQuery = true)
    long getNextValue(@Param("tenantId") UUID tenantId);
}