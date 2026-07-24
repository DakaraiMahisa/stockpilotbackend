
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
    'organization:read',
    'View organization profile',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'organization:update',
    'Update organization profile and branding',
    NOW(),
    NOW(),
    0,
    FALSE
)

ON CONFLICT (code) DO NOTHING;