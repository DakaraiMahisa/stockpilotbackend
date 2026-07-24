INSERT INTO organizations (
    tenant_id,
    legal_name,
    display_name,
    email,
    phone,
    country_code,
    created_at,
    updated_at,
    version,
    deleted
)
SELECT
    t.id,
    COALESCE(t.legal_name, t.name),
    t.name,
    u.email,
    t.phone,
    'ZW',
    NOW(),
    NOW(),
    0,
    FALSE
FROM tenants t
INNER JOIN users u
    ON u.tenant_id = t.id
INNER JOIN roles r
    ON r.id = u.role_id
WHERE r.name = 'OWNER'
AND NOT EXISTS (
    SELECT 1
    FROM organizations o
    WHERE o.tenant_id = t.id
);