
CREATE TABLE categories (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL,

    parent_id UUID,

    name VARCHAR(100) NOT NULL,

    code VARCHAR(50) NOT NULL,

    description VARCHAR(500),

    sort_order INTEGER NOT NULL DEFAULT 0,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_category_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id),

    CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_id)
        REFERENCES categories(id)
        ON DELETE RESTRICT,

    CONSTRAINT uk_category_code
        UNIQUE (tenant_id, code),

    CONSTRAINT uk_category_name_per_parent
        UNIQUE (tenant_id, parent_id, name),

    CONSTRAINT chk_category_name
        CHECK (
            LENGTH(TRIM(name)) > 0
        ),

    CONSTRAINT chk_category_code
        CHECK (
            LENGTH(TRIM(code)) > 0
        ),

    CONSTRAINT chk_category_sort_order
        CHECK (
            sort_order >= 0
        )
);


CREATE INDEX idx_category_tenant
ON categories (tenant_id);

CREATE INDEX idx_category_parent
ON categories (parent_id);

CREATE INDEX idx_category_tenant_parent
ON categories (tenant_id, parent_id);

CREATE INDEX idx_category_tenant_deleted
ON categories (tenant_id, deleted);

CREATE INDEX idx_category_sort_order
ON categories (tenant_id, sort_order);


INSERT INTO permissions (

    id,
    code,
    description,

    created_at,
    updated_at,

    version,
    deleted

)
VALUES

(
    gen_random_uuid(),
    'category:read',
    'View product categories',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'category:create',
    'Create product categories',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'category:update',
    'Update product categories',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'category:delete',
    'Delete product categories',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'category:move',
    'Move product categories within the hierarchy',
    NOW(),
    NOW(),
    0,
    FALSE
)

ON CONFLICT (code) DO NOTHING;