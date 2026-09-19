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

(gen_random_uuid(), 'catalog-pricing:read',   'View catalog pricing',   NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'catalog-pricing:create', 'Create catalog pricing', NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'catalog-pricing:update', 'Update catalog pricing', NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'catalog-pricing:delete', 'Delete catalog pricing', NOW(), NOW(), 0, FALSE)

ON CONFLICT (code) DO NOTHING;