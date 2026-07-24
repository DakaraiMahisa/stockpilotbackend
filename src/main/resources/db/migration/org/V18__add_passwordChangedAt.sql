
ALTER TABLE users
ADD COLUMN password_changed_at TIMESTAMPTZ;

UPDATE users
SET password_changed_at = created_at
WHERE password_changed_at IS NULL;

ALTER TABLE users
ALTER COLUMN password_changed_at SET NOT NULL;