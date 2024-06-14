-- liquibase formatted sql
-- changeset dabico:10

DROP MATERIALIZED VIEW IF EXISTS total_code_size_in_bytes;
CREATE MATERIALIZED VIEW total_code_size_in_bytes AS
SELECT SUM(LENGTH(content)) AS size FROM file;

CREATE UNIQUE INDEX ON total_code_size_in_bytes(size);
