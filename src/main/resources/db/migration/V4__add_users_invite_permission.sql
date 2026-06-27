INSERT INTO permissions (
    id,
    code,
    description,
    created_at,
    updated_at,
    version,
    deleted
)
VALUES (
    gen_random_uuid(),
    'users:invite',
    'Invite users to join the organization',
    NOW(),
    NOW(),
    0,
    FALSE
)
ON CONFLICT (code) DO NOTHING;