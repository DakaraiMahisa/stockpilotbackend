
-- ============================================================
-- Create tax_classes table
-- ============================================================

CREATE TABLE tax_classes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL,

    name VARCHAR(80) NOT NULL,

    code VARCHAR(20) NOT NULL,

    tax_type VARCHAR(10) NOT NULL,

    is_default BOOLEAN NOT NULL DEFAULT FALSE,

    hsn_sac_code VARCHAR(10),

    description TEXT,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_tax_class_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id),

    CONSTRAINT uk_tax_class_tenant_name
        UNIQUE (tenant_id, name),

    CONSTRAINT uk_tax_class_tenant_code
        UNIQUE (tenant_id, code),

    CONSTRAINT chk_tax_class_type
        CHECK (tax_type IN ('GST', 'VAT', 'NONE'))
);

-- ============================================================
-- Create tax_rates table
-- ============================================================

CREATE TABLE tax_rates (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL,

    tax_class_id UUID NOT NULL,

    rate_type VARCHAR(10) NOT NULL,

    rate NUMERIC(6,3) NOT NULL,

    effective_from DATE NOT NULL,

    effective_to DATE,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_tax_rate_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id),

    CONSTRAINT fk_tax_rate_tax_class
        FOREIGN KEY (tax_class_id)
        REFERENCES tax_classes(id)
        ON DELETE RESTRICT,

    CONSTRAINT uk_tax_rate_effective
        UNIQUE (
            tax_class_id,
            rate_type,
            effective_from
        ),

    CONSTRAINT chk_rate_type
        CHECK (
            rate_type IN (
                'VAT',
                'CGST',
                'SGST',
                'IGST'
            )
        ),

    CONSTRAINT chk_rate_value
        CHECK (
            rate >= 0
            AND rate <= 100
        ),

    CONSTRAINT chk_effective_dates
        CHECK (
            effective_to IS NULL
            OR effective_to >= effective_from
        )
);

-- ============================================================
-- Create indexes
-- ============================================================

CREATE INDEX idx_tax_class_tenant
ON tax_classes (tenant_id);

CREATE UNIQUE INDEX uk_tax_class_default
ON tax_classes (tenant_id)
WHERE is_default = TRUE;

CREATE INDEX idx_tax_rate_class
ON tax_rates (tax_class_id);

CREATE INDEX idx_tax_rate_tenant
ON tax_rates (tenant_id);

CREATE INDEX idx_tax_rate_effective
ON tax_rates (effective_from);


-- ============================================================
-- Backfill default GST configuration for existing organizations
-- ============================================================

INSERT INTO tax_classes (
    tenant_id,
    name,
    code,
    tax_type,
    is_default,
    created_at,
    updated_at,
    version,
    deleted
)
SELECT
    o.tenant_id,
    s.name,
    s.code,
    'GST',
    s.is_default,
    NOW(),
    NOW(),
    0,
    FALSE
FROM organizations o
CROSS JOIN (
    VALUES
        ('Zero Rated', 'GST0',  FALSE),
        ('GST 5%',     'GST5',  FALSE),
        ('GST 12%',    'GST12', FALSE),
        ('GST 18%',    'GST18', TRUE),
        ('GST 28%',    'GST28', FALSE)
) AS s(name, code, is_default)
WHERE NOT EXISTS (
    SELECT 1
    FROM tax_classes tc
    WHERE tc.tenant_id = o.tenant_id
);


-- ============================================================
-- GST Rates
-- ============================================================

INSERT INTO tax_rates (
    tenant_id,
    tax_class_id,
    rate_type,
    rate,
    effective_from,
    created_at,
    updated_at,
    version,
    deleted
)
SELECT
    tc.tenant_id,
    tc.id,
    'CGST',
    CASE tc.code
        WHEN 'GST0'  THEN 0.000
        WHEN 'GST5'  THEN 2.500
        WHEN 'GST12' THEN 6.000
        WHEN 'GST18' THEN 9.000
        WHEN 'GST28' THEN 14.000
    END,
    CURRENT_DATE,
    NOW(),
    NOW(),
    0,
    FALSE
FROM tax_classes tc
WHERE tc.tax_type = 'GST'
AND NOT EXISTS (
    SELECT 1
    FROM tax_rates tr
    WHERE tr.tax_class_id = tc.id
      AND tr.rate_type = 'CGST'
);

-- ============================================================
-- Backfill tax rates for SGST
-- ============================================================

INSERT INTO tax_rates (
    tenant_id,
    tax_class_id,
    rate_type,
    rate,
    effective_from,
    created_at,
    updated_at,
    version,
    deleted
)
SELECT
    tc.tenant_id,
    tc.id,
    'SGST',
    CASE tc.code
        WHEN 'GST0'  THEN 0.000
        WHEN 'GST5'  THEN 2.500
        WHEN 'GST12' THEN 6.000
        WHEN 'GST18' THEN 9.000
        WHEN 'GST28' THEN 14.000
    END,
    CURRENT_DATE,
    NOW(),
    NOW(),
    0,
    FALSE
FROM tax_classes tc
WHERE tc.tax_type = 'GST'
AND NOT EXISTS (
    SELECT 1
    FROM tax_rates tr
    WHERE tr.tax_class_id = tc.id
      AND tr.rate_type = 'SGST'
);

-- ============================================================
-- Insert permissions for tax configuration
-- ============================================================

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
    'tax:read',
    'View tax configuration',
    NOW(),
    NOW(),
    0,
    FALSE
),
(
    gen_random_uuid(),
    'tax:create',
    'Create tax classes',
    NOW(),
    NOW(),
    0,
    FALSE
),
(
    gen_random_uuid(),
    'tax:update',
    'Update tax configuration',
    NOW(),
    NOW(),
    0,
    FALSE
),
(
    gen_random_uuid(),
    'tax:set-default',
    'Set default tax class',
    NOW(),
    NOW(),
    0,
    FALSE
),
(
    gen_random_uuid(),
    'tax:resolve',
    'Resolve tax calculations',
    NOW(),
    NOW(),
    0,
    FALSE
)
ON CONFLICT (code) DO NOTHING;