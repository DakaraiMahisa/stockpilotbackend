-- Allow multiple verification tokens per user
ALTER TABLE verification_tokens
DROP CONSTRAINT ukdqp95ggn6gvm865km5muba2o5;

-- Foreign keys should not generate random UUIDs
ALTER TABLE verification_tokens
ALTER COLUMN user_id DROP DEFAULT;

-- Track when a verification token has been consumed
ALTER TABLE verification_tokens
ADD COLUMN used_at TIMESTAMP WITH TIME ZONE;

ALTER TABLE users
    ALTER COLUMN active SET DEFAULT TRUE;

ALTER TABLE users
    ALTER COLUMN status SET DEFAULT 'ACTIVE';

ALTER TABLE users
    ALTER COLUMN locked SET DEFAULT FALSE;

ALTER TABLE users
    ALTER COLUMN failed_login_attempts SET DEFAULT 0;

ALTER TABLE users
    ALTER COLUMN email_verified SET DEFAULT FALSE;

ALTER TABLE users
    ALTER COLUMN mfa_enabled SET DEFAULT FALSE;

-- RefreshToken inherits BaseEntity
ALTER TABLE refresh_tokens
ALTER COLUMN created_at
TYPE TIMESTAMP WITH TIME ZONE
USING created_at AT TIME ZONE 'UTC';

ALTER TABLE refresh_tokens
ALTER COLUMN updated_at
TYPE TIMESTAMP WITH TIME ZONE
USING updated_at AT TIME ZONE 'UTC';

-- Permissions inherits BaseEntity
ALTER TABLE permissions
ALTER COLUMN created_at
TYPE TIMESTAMP WITH TIME ZONE
USING created_at AT TIME ZONE 'UTC';

ALTER TABLE permissions
ALTER COLUMN updated_at
TYPE TIMESTAMP WITH TIME ZONE
USING updated_at AT TIME ZONE 'UTC';

-- Roles inherits BaseEntity
ALTER TABLE roles
ALTER COLUMN created_at
TYPE TIMESTAMP WITH TIME ZONE
USING created_at AT TIME ZONE 'UTC';

ALTER TABLE roles
ALTER COLUMN updated_at
TYPE TIMESTAMP WITH TIME ZONE
USING updated_at AT TIME ZONE 'UTC';

-- PasswordResetToken (BaseEntity timestamp standardization)

ALTER TABLE password_reset_tokens
ALTER COLUMN created_at
TYPE TIMESTAMP WITH TIME ZONE
USING created_at AT TIME ZONE 'UTC';

ALTER TABLE password_reset_tokens
ALTER COLUMN updated_at
TYPE TIMESTAMP WITH TIME ZONE
USING updated_at AT TIME ZONE 'UTC';

-- Tenant (BaseEntity timestamp standardization)

ALTER TABLE tenants
ALTER COLUMN created_at
TYPE TIMESTAMP WITH TIME ZONE
USING created_at AT TIME ZONE 'UTC';

ALTER TABLE tenants
ALTER COLUMN updated_at
TYPE TIMESTAMP WITH TIME ZONE
USING updated_at AT TIME ZONE 'UTC';

ALTER TABLE tenants
ALTER COLUMN active SET DEFAULT TRUE;

-- InvitationToken (BaseEntity timestamp standardization)

ALTER TABLE invitation_tokens
ALTER COLUMN created_at
TYPE TIMESTAMP WITH TIME ZONE
USING created_at AT TIME ZONE 'UTC';

ALTER TABLE invitation_tokens
ALTER COLUMN updated_at
TYPE TIMESTAMP WITH TIME ZONE
USING updated_at AT TIME ZONE 'UTC';

ALTER TABLE invitation_tokens
ALTER COLUMN used SET DEFAULT FALSE;
    -- UserSession (BaseEntity timestamp standardization)

ALTER TABLE user_sessions
ALTER COLUMN created_at
TYPE TIMESTAMP WITH TIME ZONE
USING created_at AT TIME ZONE 'UTC';

ALTER TABLE user_sessions
ALTER COLUMN updated_at
TYPE TIMESTAMP WITH TIME ZONE
USING updated_at AT TIME ZONE 'UTC';

ALTER TABLE user_sessions
ALTER COLUMN revoked SET DEFAULT FALSE;