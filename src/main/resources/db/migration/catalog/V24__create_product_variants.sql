
CREATE TABLE variant_attributes (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL,

    name VARCHAR(50) NOT NULL,

    code VARCHAR(30) NOT NULL,

    description VARCHAR(255),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_variant_attribute_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id),

    CONSTRAINT uk_variant_attribute_code
        UNIQUE (tenant_id, code),

    CONSTRAINT chk_variant_attribute_name
        CHECK (
            LENGTH(TRIM(name)) > 0
        ),

    CONSTRAINT chk_variant_attribute_code
        CHECK (
            LENGTH(TRIM(code)) > 0
        )
);


CREATE INDEX idx_variant_attribute_tenant
ON variant_attributes (tenant_id);

CREATE INDEX idx_variant_attribute_tenant_active
ON variant_attributes (tenant_id, active);

CREATE INDEX idx_variant_attribute_tenant_deleted
ON variant_attributes (tenant_id, deleted);

CREATE INDEX idx_variant_attribute_code
ON variant_attributes (tenant_id, code);


CREATE TABLE variant_attribute_values (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL,

    attribute_id UUID NOT NULL,

    value VARCHAR(50) NOT NULL,

    code VARCHAR(30) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    sort_order INTEGER NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_variant_attribute_value_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id),

    CONSTRAINT fk_variant_attribute_value_attribute
        FOREIGN KEY (attribute_id)
        REFERENCES variant_attributes(id),

    CONSTRAINT uk_variant_attribute_value_code
        UNIQUE (attribute_id, code),

    CONSTRAINT chk_variant_attribute_value
        CHECK (
            LENGTH(TRIM(value)) > 0
        ),

    CONSTRAINT chk_variant_attribute_value_code
        CHECK (
            LENGTH(TRIM(code)) > 0
        ),

    CONSTRAINT chk_variant_attribute_value_sort_order
        CHECK (
            sort_order >= 0
        )
);


CREATE INDEX idx_variant_attribute_value_tenant
ON variant_attribute_values (tenant_id);

CREATE INDEX idx_variant_attribute_value_attribute
ON variant_attribute_values (attribute_id);

CREATE INDEX idx_variant_attribute_value_tenant_attribute
ON variant_attribute_values (tenant_id, attribute_id);

CREATE INDEX idx_variant_attribute_value_active
ON variant_attribute_values (tenant_id, active);

CREATE INDEX idx_variant_attribute_value_deleted
ON variant_attribute_values (tenant_id, deleted);


CREATE TABLE product_variant_attributes (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL,

    product_id UUID NOT NULL,

    attribute_id UUID NOT NULL,

    sort_order INTEGER NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_product_variant_attribute_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id),

    CONSTRAINT fk_product_variant_attribute_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_product_variant_attribute_attribute
        FOREIGN KEY (attribute_id)
        REFERENCES variant_attributes(id),

    CONSTRAINT uk_product_variant_attribute
        UNIQUE (product_id, attribute_id),

    CONSTRAINT chk_product_variant_attribute_sort_order
        CHECK (
            sort_order >= 0
        )
);


CREATE INDEX idx_product_variant_attribute_tenant
ON product_variant_attributes (tenant_id);

CREATE INDEX idx_product_variant_attribute_product
ON product_variant_attributes (product_id);

CREATE INDEX idx_product_variant_attribute_attribute
ON product_variant_attributes (attribute_id);

CREATE INDEX idx_product_variant_attribute_tenant_product
ON product_variant_attributes (tenant_id, product_id);

CREATE INDEX idx_product_variant_attribute_tenant_deleted
ON product_variant_attributes (tenant_id, deleted);


CREATE TABLE product_variants (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL,

    product_id UUID NOT NULL,

    sku VARCHAR(60) NOT NULL,

    barcode VARCHAR(60),

    attributes JSONB NOT NULL,

    cost_price NUMERIC(14,4) NOT NULL DEFAULT 0.0000,

    retail_price NUMERIC(14,4) NOT NULL DEFAULT 0.0000,

    image_url VARCHAR(500),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    additional_weight_kg NUMERIC(8,3) NOT NULL DEFAULT 0.000,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_product_variant_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id),

    CONSTRAINT fk_product_variant_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_product_variant_sku
        UNIQUE (tenant_id, sku),

    CONSTRAINT uk_product_variant_barcode
        UNIQUE (tenant_id, barcode),

    CONSTRAINT chk_product_variant_sku
        CHECK (
            LENGTH(TRIM(sku)) > 0
        ),

    CONSTRAINT chk_product_variant_attributes
        CHECK (
            jsonb_typeof(attributes) = 'object'
            AND attributes <> '{}'::jsonb
        ),

    CONSTRAINT chk_product_variant_cost_price
        CHECK (
            cost_price >= 0
        ),

    CONSTRAINT chk_product_variant_retail_price
        CHECK (
            retail_price >= cost_price
        ),

    CONSTRAINT chk_product_variant_weight
        CHECK (
            additional_weight_kg >= 0
        )
);


CREATE INDEX idx_product_variant_tenant
ON product_variants (tenant_id);

CREATE INDEX idx_product_variant_product
ON product_variants (product_id);

CREATE INDEX idx_product_variant_tenant_product
ON product_variants (tenant_id, product_id);

CREATE INDEX idx_product_variant_tenant_active
ON product_variants (tenant_id, active);

CREATE INDEX idx_product_variant_tenant_deleted
ON product_variants (tenant_id, deleted);

CREATE INDEX idx_product_variant_sku
ON product_variants (tenant_id, sku);

CREATE INDEX idx_product_variant_barcode
ON product_variants (tenant_id, barcode);

CREATE INDEX idx_product_variant_attributes
ON product_variants
USING GIN (attributes);



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
    'variant:read',
    'View product variants and variant attributes',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'variant:create',
    'Create product variants and configure variant attributes',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'variant:update',
    'Update product variants and variant attributes',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'variant:delete',
    'Deactivate product variants and variant attributes',
    NOW(),
    NOW(),
    0,
    FALSE
)

ON CONFLICT (code) DO NOTHING;