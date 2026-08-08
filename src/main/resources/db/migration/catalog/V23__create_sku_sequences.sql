CREATE TABLE sku_sequences (

    tenant_id UUID PRIMARY KEY,

    next_value BIGINT NOT NULL DEFAULT 1,

    CONSTRAINT fk_sku_sequence_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenants(id),

    CONSTRAINT chk_sku_sequence_next_value
        CHECK (next_value > 0)
);