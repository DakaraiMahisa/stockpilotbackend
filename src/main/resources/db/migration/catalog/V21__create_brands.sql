CREATE TABLE brands (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL,

    name VARCHAR(100) NOT NULL,

    code VARCHAR(20) NOT NULL,

    logo_object_key VARCHAR(500),

    website VARCHAR(200),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_brand_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id),

    CONSTRAINT uk_brand_name
        UNIQUE (tenant_id, name),

    CONSTRAINT uk_brand_code
        UNIQUE (tenant_id, code),

    CONSTRAINT chk_brand_name
        CHECK (
            LENGTH(TRIM(name)) > 0
        ),

    CONSTRAINT chk_brand_code
        CHECK (
            LENGTH(TRIM(code)) > 0
        ),

    CONSTRAINT chk_brand_website
            CHECK (
                website IS NULL
                OR LENGTH(TRIM(website)) > 0
            )
);

CREATE INDEX idx_brand_tenant
ON brands (tenant_id);

CREATE INDEX idx_brand_tenant_active
ON brands (tenant_id, active);

CREATE INDEX idx_brand_tenant_deleted
ON brands (tenant_id, deleted);

CREATE INDEX idx_brand_name
ON brands (tenant_id, name);

CREATE INDEX idx_brand_code
ON brands (tenant_id, code);

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
    'brand:read',
    'View product brands',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'brand:create',
    'Create product brands',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'brand:update',
    'Update product brands',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'brand:delete',
    'Delete product brands',
    NOW(),
    NOW(),
    0,
    FALSE
)

ON CONFLICT (code) DO NOTHING;