
ALTER TABLE branches
    ADD CONSTRAINT uk_branches_id_tenant
        UNIQUE (id, tenant_id);

CREATE TABLE inventory_warehouses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    branch_id UUID NOT NULL,

    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL,
    address_line VARCHAR(255),

    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    bin_tracking_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_inventory_warehouse_branch_tenant
        FOREIGN KEY (branch_id, tenant_id)
            REFERENCES branches (id, tenant_id)
            ON DELETE RESTRICT,

    CONSTRAINT uk_inventory_warehouse_tenant_branch_code
        UNIQUE (tenant_id, branch_id, code),

    CONSTRAINT chk_inventory_warehouse_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED')),

    CONSTRAINT chk_inventory_warehouse_name
        CHECK (LENGTH(TRIM(name)) > 0),

    CONSTRAINT chk_inventory_warehouse_code
        CHECK (LENGTH(TRIM(code)) > 0)
);

CREATE UNIQUE INDEX uk_inventory_warehouse_default_branch
    ON inventory_warehouses (tenant_id, branch_id)
    WHERE is_default = TRUE AND deleted = FALSE;

CREATE INDEX idx_inventory_warehouses_tenant_branch
    ON inventory_warehouses (tenant_id, branch_id)
    WHERE deleted = FALSE;

CREATE INDEX idx_inventory_warehouses_tenant_status
    ON inventory_warehouses (tenant_id, status)
    WHERE deleted = FALSE;

