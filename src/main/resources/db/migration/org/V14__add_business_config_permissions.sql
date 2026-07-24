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
    'business-config:read',
    'View business configuration',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'business-config:update',
    'Update business configuration',
    NOW(),
    NOW(),
    0,
    FALSE
)

ON CONFLICT (code) DO NOTHING;