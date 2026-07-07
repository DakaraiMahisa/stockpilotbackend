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
    'branches:read',
    'View branches',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'branches:create',
    'Create new branches',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'branches:update',
    'Update branch information',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'branches:update-status',
    'Change branch operational status',
    NOW(),
    NOW(),
    0,
    FALSE
),

(
    gen_random_uuid(),
    'branches:set-default',
    'Set the default branch',
    NOW(),
    NOW(),
    0,
    FALSE
)

ON CONFLICT (code) DO NOTHING;