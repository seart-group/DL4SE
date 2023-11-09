-- liquibase formatted sql
-- changeset dabico:2

CREATE OR REPLACE VIEW language_extension AS
SELECT
    id AS lang_id,
    UNNEST(extensions) AS extension
FROM language;

CREATE OR REPLACE VIEW file_content_hash_distinct AS
SELECT
    MIN(id) AS id,
    content_hash AS hash
FROM file
GROUP BY content_hash;

CREATE OR REPLACE VIEW file_ast_hash_distinct AS
SELECT
    MIN(id) AS id,
    ast_hash AS hash
FROM file
GROUP BY ast_hash;

CREATE OR REPLACE VIEW function_content_hash_distinct AS
SELECT
    MIN(id) AS id,
    content_hash AS hash
FROM function
GROUP BY content_hash;

CREATE OR REPLACE VIEW function_ast_hash_distinct AS
SELECT
    MIN(id) AS id,
    ast_hash AS hash
FROM function
GROUP BY ast_hash;
