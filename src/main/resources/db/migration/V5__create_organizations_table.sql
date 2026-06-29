CREATE TABLE organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL,

    legal_name VARCHAR(200) NOT NULL,
    display_name VARCHAR(120) NOT NULL,

    email VARCHAR(150) NOT NULL,
    phone VARCHAR(20),

    address_line1 VARCHAR(200),
    address_line2 VARCHAR(200),

    city VARCHAR(100),
    state_province VARCHAR(100),
    postal_code VARCHAR(20),

    country_code CHAR(2) NOT NULL DEFAULT 'ZW',

    gstin_vat_number VARCHAR(20),

    logo_url VARCHAR(500),
    website VARCHAR(200),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    version BIGINT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_organization_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_organization_tenant
        UNIQUE (tenant_id),

    CONSTRAINT chk_org_country
        CHECK (length(country_code) = 2),

    CONSTRAINT chk_org_email
        CHECK (email <> ''),

    CONSTRAINT chk_org_legal_name
        CHECK (legal_name <> ''),

    CONSTRAINT chk_org_display_name
        CHECK (display_name <> '')
);