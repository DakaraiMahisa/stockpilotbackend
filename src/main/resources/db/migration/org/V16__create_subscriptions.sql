-- ============================================================
-- Create subscriptions table
-- ============================================================

CREATE TABLE subscriptions (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tenant_id UUID NOT NULL,

    plan_code VARCHAR(20) NOT NULL,

    status VARCHAR(20) NOT NULL,

    plan_started_at TIMESTAMP NOT NULL,

    trial_ends_at TIMESTAMP,

    plan_expires_at TIMESTAMP,

    max_users INTEGER NOT NULL,

    max_branches INTEGER NOT NULL,

    max_skus INTEGER NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    version BIGINT NOT NULL DEFAULT 0,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_subscription_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id),

    CONSTRAINT uk_subscription_tenant
        UNIQUE (tenant_id),

    CONSTRAINT chk_subscription_plan
        CHECK (
            plan_code IN (
                'TRIAL',
                'BASIC',
                'PROFESSIONAL'
            )
        ),

    CONSTRAINT chk_subscription_status
        CHECK (
            status IN (
                'TRIAL',
                'ACTIVE',
                'EXPIRED',
                'SUSPENDED'
            )
        ),

    CONSTRAINT chk_subscription_limits
        CHECK (
            max_users > 0
            AND max_branches > 0
            AND max_skus >= -1
        )
);

-- ============================================================
-- Create indexes
-- ============================================================

CREATE INDEX idx_subscription_tenant
ON subscriptions (tenant_id);

CREATE INDEX idx_subscription_plan
ON subscriptions (plan_code);

CREATE INDEX idx_subscription_status
ON subscriptions (status);

CREATE INDEX idx_subscription_trial_end
ON subscriptions (trial_ends_at);

CREATE INDEX idx_subscription_active
ON subscriptions (active);

-- ============================================================
-- Backfill subscriptions for existing tenants
-- ============================================================

INSERT INTO subscriptions (

    tenant_id,

    plan_code,

    status,

    plan_started_at,

    trial_ends_at,

    plan_expires_at,

    max_users,

    max_branches,

    max_skus,

    active,

    created_at,

    updated_at,

    version,

    deleted

)

SELECT

    t.id,

    'TRIAL',

    'TRIAL',

    NOW(),

    NOW() + INTERVAL '14 days',

    NULL,

    3,

    1,

    100,

    TRUE,

    NOW(),

    NOW(),

    0,

    FALSE

FROM tenants t

WHERE NOT EXISTS (

    SELECT 1

    FROM subscriptions s

    WHERE s.tenant_id = t.id

);

-- ============================================================
-- Insert subscription permissions
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
    'subscription:read',
    'View current subscription',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'subscription:upgrade',
    'Submit subscription upgrade request',
    NOW(),
    NOW(),
    0,
    FALSE
)

ON CONFLICT (code) DO NOTHING;