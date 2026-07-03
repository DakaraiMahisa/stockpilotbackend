CREATE TABLE branches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL,

    name VARCHAR(100) NOT NULL,

    code VARCHAR(10) NOT NULL,

    branch_type VARCHAR(20) NOT NULL DEFAULT 'RETAIL',

    phone VARCHAR(20),

    email VARCHAR(150),

    address_line_1 VARCHAR(200),

    city VARCHAR(100),

    is_default BOOLEAN NOT NULL DEFAULT FALSE,

    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',

    manager_id UUID,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    version BIGINT NOT NULL DEFAULT 0,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_branches_organization
        FOREIGN KEY (tenant_id)
        REFERENCES organizations(id),

    CONSTRAINT fk_branches_manager
        FOREIGN KEY (manager_id)
        REFERENCES users(id),

    CONSTRAINT uk_branches_tenant_name
        UNIQUE (tenant_id, name),

    CONSTRAINT uk_branches_tenant_code
        UNIQUE (tenant_id, code),

    CONSTRAINT chk_branch_type
        CHECK (
            branch_type IN (
                'RETAIL',
                'WHOLESALE',
                'WAREHOUSE',
                'ONLINE'
            )
        ),

    CONSTRAINT chk_branch_status
        CHECK (
            status IN (
                'DRAFT',
                'ACTIVE',
                'INACTIVE',
                'ARCHIVED'
            )
        )
);

-- ==========================================================
-- Indexes
-- ==========================================================

CREATE INDEX idx_branches_tenant_deleted
    ON branches (tenant_id, deleted);

CREATE INDEX idx_branches_tenant_status_deleted
    ON branches (tenant_id, status, deleted);

CREATE INDEX idx_branches_manager
    ON branches (manager_id);

CREATE INDEX idx_branches_code
    ON branches (code);

-- ==========================================================
-- Exactly one ACTIVE default branch per tenant
-- ==========================================================

CREATE UNIQUE INDEX idx_branch_default
    ON branches (tenant_id)
    WHERE is_default = TRUE
      AND status = 'ACTIVE'
      AND deleted = FALSE;