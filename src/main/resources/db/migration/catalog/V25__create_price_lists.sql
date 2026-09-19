
CREATE TABLE price_lists (

    id UUID NOT NULL,

    tenant_id UUID NOT NULL,

    name VARCHAR(100) NOT NULL,

    code VARCHAR(20) NOT NULL,

    price_list_type VARCHAR(20) NOT NULL DEFAULT 'RETAIL',

    currency_code CHAR(3) NOT NULL,

    is_default BOOLEAN NOT NULL DEFAULT FALSE,

    valid_from DATE,

    valid_to DATE,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_price_lists
        PRIMARY KEY (id),

    CONSTRAINT fk_price_list_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id),

    CONSTRAINT chk_price_list_type
        CHECK (
            price_list_type IN (
                'RETAIL',
                'WHOLESALE',
                'STAFF',
                'SPECIAL',
                'PROMOTIONAL'
            )
        ),

    CONSTRAINT chk_price_list_code
        CHECK (
            LENGTH(TRIM(code)) > 0
            AND code = UPPER(code)
        ),

    CONSTRAINT chk_price_list_currency
        CHECK (
            LENGTH(TRIM(currency_code)) = 3
            AND currency_code = UPPER(currency_code)
        ),

    CONSTRAINT chk_price_list_validity
        CHECK (
            valid_to IS NULL
            OR valid_from IS NULL
            OR valid_to > valid_from
        )
);


CREATE INDEX idx_price_list_tenant
ON price_lists (tenant_id);

CREATE INDEX idx_price_list_tenant_active
ON price_lists (tenant_id, active);

CREATE INDEX idx_price_list_tenant_type
ON price_lists (tenant_id, price_list_type);

CREATE UNIQUE INDEX uk_price_list_tenant_code
ON price_lists (tenant_id, code)
WHERE deleted = FALSE;

CREATE UNIQUE INDEX uk_price_list_tenant_default
ON price_lists (tenant_id)
WHERE is_default = TRUE
  AND deleted = FALSE;


CREATE TABLE price_list_items (

    id UUID NOT NULL,

    tenant_id UUID NOT NULL,

    price_list_id UUID NOT NULL,

    product_id UUID,

    variant_id UUID,

    min_quantity NUMERIC(12,3) NOT NULL DEFAULT 1,

    unit_price NUMERIC(14,4) NOT NULL DEFAULT 0,

    discount_pct NUMERIC(5,2) NOT NULL DEFAULT 0,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_price_list_items
        PRIMARY KEY (id),

    CONSTRAINT fk_price_list_item_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id),

    CONSTRAINT fk_price_list_item_price_list
        FOREIGN KEY (price_list_id)
        REFERENCES price_lists(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_price_list_item_product
        FOREIGN KEY (product_id)
        REFERENCES products(id),

    CONSTRAINT fk_price_list_item_variant
        FOREIGN KEY (variant_id)
        REFERENCES product_variants(id),

    CONSTRAINT chk_price_list_item_target
        CHECK (
            (product_id IS NOT NULL AND variant_id IS NULL)
            OR
            (product_id IS NULL AND variant_id IS NOT NULL)
        ),

    CONSTRAINT chk_price_list_item_min_quantity
        CHECK (
            min_quantity >= 1
        ),

    CONSTRAINT chk_price_list_item_unit_price
        CHECK (
            unit_price >= 0
        ),

    CONSTRAINT chk_price_list_item_discount_pct
        CHECK (
            discount_pct BETWEEN 0 AND 100
        )
);


CREATE INDEX idx_price_list_item_tenant
ON price_list_items (tenant_id);

CREATE INDEX idx_price_list_item_price_list
ON price_list_items (price_list_id);

CREATE INDEX idx_price_list_item_tenant_product_quantity
ON price_list_items (
    tenant_id,
    product_id,
    min_quantity
);

CREATE INDEX idx_price_list_item_tenant_variant_quantity
ON price_list_items (
    tenant_id,
    variant_id,
    min_quantity
);