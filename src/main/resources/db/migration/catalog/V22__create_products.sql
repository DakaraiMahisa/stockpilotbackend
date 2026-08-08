CREATE TABLE products (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL,

    sku VARCHAR(50) NOT NULL,

    barcode VARCHAR(60),

    name VARCHAR(200) NOT NULL,

    description TEXT,

    search_vector TSVECTOR,

    brand_id UUID,

    category_id UUID NOT NULL,

    tax_class_id UUID NOT NULL,

    unit_of_measure VARCHAR(20) NOT NULL DEFAULT 'PCS',

    track_batches BOOLEAN NOT NULL DEFAULT FALSE,

    has_variants BOOLEAN NOT NULL DEFAULT FALSE,

    min_stock_level INTEGER NOT NULL DEFAULT 0,

    max_stock_level INTEGER,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    service BOOLEAN NOT NULL DEFAULT FALSE,

    weight_kg NUMERIC(8,3),

    notes TEXT,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_product_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id),

    CONSTRAINT fk_product_brand
        FOREIGN KEY (brand_id)
        REFERENCES brands(id),

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id),

    CONSTRAINT fk_product_tax_class
        FOREIGN KEY (tax_class_id)
        REFERENCES tax_classes(id),

    CONSTRAINT uk_product_sku
        UNIQUE (tenant_id, sku),

    CONSTRAINT uk_product_barcode
        UNIQUE (tenant_id, barcode),

    CONSTRAINT chk_product_name
        CHECK (
            LENGTH(TRIM(name)) > 0
        ),

    CONSTRAINT chk_product_sku
        CHECK (
            LENGTH(TRIM(sku)) > 0
        ),

    CONSTRAINT chk_product_min_stock
        CHECK (
            min_stock_level >= 0
        ),

    CONSTRAINT chk_product_max_stock
        CHECK (
            max_stock_level IS NULL
            OR max_stock_level > min_stock_level
        ),

    CONSTRAINT chk_product_weight
        CHECK (
            weight_kg IS NULL
            OR weight_kg >= 0
        )
);

CREATE INDEX idx_product_tenant
ON products (tenant_id);

CREATE INDEX idx_product_tenant_active
ON products (tenant_id, active);

CREATE INDEX idx_product_tenant_deleted
ON products (tenant_id, deleted);

CREATE INDEX idx_product_category
ON products (tenant_id, category_id);

CREATE INDEX idx_product_brand
ON products (tenant_id, brand_id);

CREATE INDEX idx_product_tax_class
ON products (tenant_id, tax_class_id);

CREATE INDEX idx_product_name
ON products (tenant_id, name);

CREATE INDEX idx_product_search
ON products
USING GIN (search_vector);

CREATE FUNCTION products_search_vector_update()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN

    NEW.search_vector :=
        setweight(
            to_tsvector(
                'english',
                COALESCE(NEW.name, '')
            ),
            'A'
        )
        ||
        setweight(
            to_tsvector(
                'english',
                COALESCE(NEW.sku, '')
            ),
            'A'
        )
        ||
        setweight(
            to_tsvector(
                'english',
                COALESCE(NEW.barcode, '')
            ),
            'A'
        )
        ||
        setweight(
            to_tsvector(
                'english',
                COALESCE(NEW.description, '')
            ),
            'B'
        );

    RETURN NEW;

END;
$$;

CREATE TRIGGER trg_products_search_vector
BEFORE INSERT OR UPDATE OF name, description, sku, barcode
ON products
FOR EACH ROW
EXECUTE FUNCTION products_search_vector_update();

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
    'product:read',
    'View products',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'product:create',
    'Create products',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'product:update',
    'Update products',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'product:delete',
    'Deactivate products',
    NOW(),
    NOW(),
    0,
    FALSE
)

ON CONFLICT (code) DO NOTHING;