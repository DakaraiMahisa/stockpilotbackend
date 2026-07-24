-- ============================================================
-- V3__seed_permission_catalog.sql
-- Seeds the global permission catalog.
-- Roles are provisioned in Java.
-- Existing OWNER roles are synchronized on application startup.
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

-- ============================================================
-- User Management
-- ============================================================

(gen_random_uuid(), 'users:read',        'View users',                    NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'users:create',      'Create users',                  NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'users:update',      'Update users',                  NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'users:delete',      'Delete users',                  NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'users:activate',    'Activate users',                NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'users:deactivate',  'Deactivate users',              NOW(), NOW(), 0, FALSE),

-- ============================================================
-- Role Management
-- ============================================================

(gen_random_uuid(), 'roles:read',        'View roles',                    NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'roles:update',      'Manage role assignments',       NOW(), NOW(), 0, FALSE),

-- ============================================================
-- Session Management
-- ============================================================

(gen_random_uuid(), 'sessions:read',     'View active sessions',          NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'sessions:revoke',   'Revoke active sessions',        NOW(), NOW(), 0, FALSE),

-- ============================================================
-- Inventory
-- ============================================================

(gen_random_uuid(), 'inventory:read',    'View inventory',                NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'inventory:create',  'Create inventory items',        NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'inventory:update',  'Update inventory items',        NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'inventory:delete',  'Delete inventory items',        NOW(), NOW(), 0, FALSE),

-- ============================================================
-- Products
-- ============================================================

(gen_random_uuid(), 'products:read',     'View products',                 NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'products:create',   'Create products',               NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'products:update',   'Update products',               NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'products:delete',   'Delete products',               NOW(), NOW(), 0, FALSE),

-- ============================================================
-- Categories
-- ============================================================

(gen_random_uuid(), 'categories:read',   'View categories',               NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'categories:create', 'Create categories',             NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'categories:update', 'Update categories',             NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'categories:delete', 'Delete categories',             NOW(), NOW(), 0, FALSE),

-- ============================================================
-- Suppliers
-- ============================================================

(gen_random_uuid(), 'suppliers:read',    'View suppliers',                NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'suppliers:create',  'Create suppliers',              NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'suppliers:update',  'Update suppliers',              NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'suppliers:delete',  'Delete suppliers',              NOW(), NOW(), 0, FALSE),

-- ============================================================
-- Purchases
-- ============================================================

(gen_random_uuid(), 'purchases:read',    'View purchases',                NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'purchases:create',  'Create purchases',              NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'purchases:update',  'Update purchases',              NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'purchases:approve', 'Approve purchases',             NOW(), NOW(), 0, FALSE),

-- ============================================================
-- Sales
-- ============================================================

(gen_random_uuid(), 'sales:read',        'View sales',                    NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'sales:create',      'Create sales',                  NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'sales:update',      'Update sales',                  NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'sales:refund',      'Refund sales',                  NOW(), NOW(), 0, FALSE),

-- ============================================================
-- Reports
-- ============================================================

(gen_random_uuid(), 'reports:read',      'View reports',                  NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'reports:export',    'Export reports',                NOW(), NOW(), 0, FALSE),

-- ============================================================
-- Settings
-- ============================================================

(gen_random_uuid(), 'settings:read',     'View tenant settings',          NOW(), NOW(), 0, FALSE),
(gen_random_uuid(), 'settings:update',   'Update tenant settings',        NOW(), NOW(), 0, FALSE)

ON CONFLICT (code) DO NOTHING;