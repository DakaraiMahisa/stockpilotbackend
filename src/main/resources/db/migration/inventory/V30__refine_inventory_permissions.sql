-- Replace broad inventory permissions with granular permissions.
-- Default grants are managed by DefaultRolePermissionPolicy.

-- 1. Add the new permission catalog entries.
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
        'inventory:stock:read',
        'View inventory stock and balances',
        NOW(), NOW(), 0, FALSE
    ),
    (
        gen_random_uuid(),
        'inventory:warehouse:read',
        'View inventory warehouses',
        NOW(), NOW(), 0, FALSE
    ),
    (
        gen_random_uuid(),
        'inventory:warehouse:write',
        'Create, update, and deactivate inventory warehouses',
        NOW(), NOW(), 0, FALSE
    ),
    (
        gen_random_uuid(),
        'inventory:bin:read',
        'View warehouse bin locations',
        NOW(), NOW(), 0, FALSE
    ),
    (
        gen_random_uuid(),
        'inventory:bin:write',
        'Create and update warehouse bin locations',
        NOW(), NOW(), 0, FALSE
    )
    ON CONFLICT (code) DO NOTHING;

-- 2. Remove assignments to obsolete inventory permissions.
DELETE FROM role_permissions
WHERE permission_id IN (
    SELECT id
    FROM permissions
    WHERE code IN (
                   'inventory:read',
                   'inventory:create',
                   'inventory:update',
                   'inventory:delete'
        )
);

-- 3. Remove the obsolete permission catalog entries.
DELETE FROM permissions
WHERE code IN (
               'inventory:read',
               'inventory:create',
               'inventory:update',
               'inventory:delete'
    );
