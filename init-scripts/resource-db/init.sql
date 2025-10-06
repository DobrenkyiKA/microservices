CREATE TABLE resource_info (
    id SERIAL PRIMARY KEY,
    key VARCHAR(255),
    storage_state VARCHAR(50) NOT NULL DEFAULT 'STAGING',
    storage_id BIGINT
);