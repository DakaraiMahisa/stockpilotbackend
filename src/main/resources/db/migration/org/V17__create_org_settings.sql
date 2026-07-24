-- ============================================================
-- Create org_settings table
-- ============================================================

CREATE TABLE org_settings (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL,

    organization_id UUID NOT NULL,

    -- ============================================================
    -- Password Policy
    -- ============================================================

    min_password_length INTEGER NOT NULL DEFAULT 8,

    require_uppercase BOOLEAN NOT NULL DEFAULT FALSE,

    require_number BOOLEAN NOT NULL DEFAULT TRUE,

    require_special_char BOOLEAN NOT NULL DEFAULT FALSE,

    password_expiry_days INTEGER NOT NULL DEFAULT 0,

    max_login_attempts INTEGER NOT NULL DEFAULT 5,

    lockout_duration_mins INTEGER NOT NULL DEFAULT 30,

    -- ============================================================
    -- Session Policy
    -- ============================================================

    session_timeout_mins INTEGER NOT NULL DEFAULT 60,

    max_concurrent_sessions INTEGER NOT NULL DEFAULT 3,

    remember_me_days INTEGER NOT NULL DEFAULT 7,

    enforce_device_trust BOOLEAN NOT NULL DEFAULT FALSE,

    -- ============================================================
    -- Invitation Policy
    -- ============================================================

    invite_expiry_hours INTEGER NOT NULL DEFAULT 48,

    allow_self_registration BOOLEAN NOT NULL DEFAULT FALSE,

    require_email_verification BOOLEAN NOT NULL DEFAULT TRUE,

    -- ============================================================
    -- General
    -- ============================================================

    default_language CHAR(5) NOT NULL DEFAULT 'en-US',

    default_timezone VARCHAR(50) NOT NULL DEFAULT 'UTC',

    maintenance_mode BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_org_settings_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id),

    CONSTRAINT fk_org_settings_organization
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_org_settings_tenant
        UNIQUE (tenant_id),

    CONSTRAINT uk_org_settings_organization
        UNIQUE (organization_id),

    -- Password Policy

    CONSTRAINT chk_min_password_length
        CHECK (
            min_password_length BETWEEN 8 AND 32
        ),

    CONSTRAINT chk_password_expiry_days
        CHECK (
            password_expiry_days IN (0, 30, 60, 90)
        ),

    CONSTRAINT chk_max_login_attempts
        CHECK (
            max_login_attempts BETWEEN 3 AND 10
        ),

    CONSTRAINT chk_lockout_duration
        CHECK (
            lockout_duration_mins BETWEEN 5 AND 1440
        ),

    -- Session Policy

    CONSTRAINT chk_session_timeout
        CHECK (
            session_timeout_mins BETWEEN 5 AND 480
        ),

    CONSTRAINT chk_max_concurrent_sessions
        CHECK (
            max_concurrent_sessions BETWEEN 1 AND 5
        ),

    CONSTRAINT chk_remember_me_days
        CHECK (
            remember_me_days BETWEEN 0 AND 30
        ),

    -- Invitation Policy

    CONSTRAINT chk_invite_expiry
        CHECK (
            invite_expiry_hours BETWEEN 1 AND 168
        )
);

-- ============================================================
-- Create indexes
-- ============================================================

CREATE INDEX idx_org_settings_tenant
ON org_settings (tenant_id);

CREATE INDEX idx_org_settings_organization
ON org_settings (organization_id);

-- ============================================================
-- Backfill existing organizations
-- ============================================================

INSERT INTO org_settings (

    tenant_id,
    organization_id,

    created_at,
    updated_at,

    version,
    deleted
)
SELECT

    o.tenant_id,
    o.id,

    NOW(),
    NOW(),

    0,
    FALSE

FROM organizations o

WHERE NOT EXISTS (

    SELECT 1
    FROM org_settings os
    WHERE os.tenant_id = o.tenant_id

);

-- ============================================================
-- Insert permissions
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
    'settings:read',
    'View organization settings',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'settings:update-password-policy',
    'Update password policy',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'settings:update-session-policy',
    'Update session policy',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'settings:update-invite-policy',
    'Update invitation policy',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'settings:update-general',
    'Update general organization settings',
    NOW(),
    NOW(),
    0,
    FALSE
)

ON CONFLICT (code) DO NOTHING;