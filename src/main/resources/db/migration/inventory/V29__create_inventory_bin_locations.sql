
ALTER TABLE inventory_warehouses
    ADD CONSTRAINT uk_inventory_warehouse_id_tenant
        UNIQUE (id, tenant_id);

CREATE TABLE inventory_bin_locations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    parent_bin_id UUID,
    code VARCHAR(30) NOT NULL,
    name VARCHAR(100),
    bin_type VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_bin_location_warehouse_tenant
        FOREIGN KEY (warehouse_id, tenant_id)
            REFERENCES inventory_warehouses (id, tenant_id)
            ON DELETE RESTRICT,

    CONSTRAINT uk_bin_location_warehouse_code
        UNIQUE (warehouse_id, code),

    CONSTRAINT chk_bin_location_type
        CHECK (bin_type IN ('ZONE', 'AISLE', 'RACK', 'SHELF', 'BIN')),

    CONSTRAINT chk_bin_location_code
        CHECK (LENGTH(TRIM(code)) > 0),

    CONSTRAINT chk_bin_location_name
        CHECK (name IS NULL OR LENGTH(TRIM(name)) > 0),

    CONSTRAINT chk_bin_location_not_own_parent
        CHECK (parent_bin_id IS NULL OR parent_bin_id <> id)
);

-- Enforce that a parent belongs to the same warehouse and tenant.
ALTER TABLE inventory_bin_locations
    ADD CONSTRAINT uk_bin_location_id_warehouse_tenant
        UNIQUE (id, warehouse_id, tenant_id);

ALTER TABLE inventory_bin_locations
    ADD CONSTRAINT fk_bin_location_parent_same_warehouse
        FOREIGN KEY (parent_bin_id, warehouse_id, tenant_id)
            REFERENCES inventory_bin_locations (id, warehouse_id, tenant_id)
            ON DELETE RESTRICT;

CREATE INDEX idx_bin_locations_tenant_warehouse
    ON inventory_bin_locations (tenant_id, warehouse_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_bin_locations_parent
    ON inventory_bin_locations (parent_bin_id)
    WHERE parent_bin_id IS NOT NULL AND deleted = FALSE;
