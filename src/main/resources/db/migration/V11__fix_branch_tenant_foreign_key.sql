ALTER TABLE branches
DROP CONSTRAINT fk_branches_organization;

ALTER TABLE branches
ADD CONSTRAINT fk_branches_tenant
FOREIGN KEY (tenant_id)
REFERENCES tenants(id)
ON DELETE RESTRICT;