-- ============================================================
-- V13__create_business_configs.sql
-- Module 2 - Organization Administration
-- Creates Business Configuration table and provisions defaults
-- for all existing organizations.
-- ============================================================

CREATE TABLE business_configs (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL,
    organization_id UUID NOT NULL,

    timezone VARCHAR(50) NOT NULL DEFAULT 'UTC',

    currency_code VARCHAR(3) NOT NULL DEFAULT 'USD',

    currency_symbol VARCHAR(5) NOT NULL DEFAULT '$',

    currency_position VARCHAR(10) NOT NULL DEFAULT 'PREFIX',

    date_format VARCHAR(20) NOT NULL DEFAULT 'dd/MM/yyyy',

    time_format VARCHAR(10) NOT NULL DEFAULT 'H12',

    number_format VARCHAR(20) NOT NULL DEFAULT 'DOT_COMMA',

    decimal_places INTEGER NOT NULL DEFAULT 2,

    fiscal_year_start VARCHAR(5) NOT NULL DEFAULT '01-01',

    default_language VARCHAR(5) NOT NULL DEFAULT 'en-US',

    weight_unit VARCHAR(5) NOT NULL DEFAULT 'KG',

    dimension_unit VARCHAR(5) NOT NULL DEFAULT 'CM',

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    version BIGINT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_business_configs_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_business_configs_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_business_configs_organization
        UNIQUE (organization_id),

    CONSTRAINT chk_business_configs_decimal_places
        CHECK (decimal_places BETWEEN 0 AND 4),

    CONSTRAINT chk_business_configs_currency_position
        CHECK (currency_position IN ('PREFIX', 'SUFFIX')),

    CONSTRAINT chk_business_configs_time_format
        CHECK (time_format IN ('H12', 'H24')),

    CONSTRAINT chk_business_configs_number_format
        CHECK (number_format IN ('DOT_COMMA', 'COMMA_DOT')),

    CONSTRAINT chk_business_configs_weight_unit
        CHECK (weight_unit IN ('KG', 'LB', 'G')),

    CONSTRAINT chk_business_configs_dimension_unit
        CHECK (dimension_unit IN ('CM', 'MM', 'IN'))
);

CREATE INDEX idx_business_configs_tenant_id
    ON business_configs (tenant_id);

INSERT INTO business_configs (
    tenant_id,
    organization_id,
    timezone,
    currency_code,
    currency_symbol,
    currency_position,
    date_format,
    time_format,
    number_format,
    decimal_places,
    fiscal_year_start,
    default_language,
    weight_unit,
    dimension_unit,
    created_at,
    updated_at,
    version,
    deleted
)
SELECT
    o.tenant_id,
    o.id,
    'UTC',
    'USD',
    '$',
    'PREFIX',
    'dd/MM/yyyy',
    'H12',
    'DOT_COMMA',
    2,
    '01-01',
    'en-US',
    'KG',
    'CM',
    NOW(),
    NOW(),
    0,
    FALSE
FROM organizations o
WHERE NOT EXISTS (
    SELECT 1
    FROM business_configs bc
    WHERE bc.organization_id = o.id
);