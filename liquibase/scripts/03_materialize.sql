-- liquibase formatted sql
-- changeset dabico:4

CREATE MATERIALIZED VIEW table_row_count AS
SELECT
    'user' AS "table",
    COUNT(id) AS "count"
FROM "user"
UNION
SELECT
    'task' AS "table",
    COUNT(id) AS "count"
FROM task
UNION
SELECT
    'git_repo' AS "table",
    COUNT(id) AS "count"
FROM git_repo
UNION
SELECT
    'file' AS "table",
    COUNT(id) AS "count"
FROM file
UNION
SELECT
    'function' AS "table",
    COUNT(id) AS "count"
FROM function;

CREATE MATERIALIZED VIEW git_repo_count_by_language AS
SELECT
    language.id AS lang_id,
    COUNT(language.id) AS count
FROM git_repo
INNER JOIN git_repo_language
    ON git_repo.id = git_repo_language.repo_id
INNER JOIN language
    ON language.id = git_repo_language.lang_id
GROUP BY language.id;

CREATE MATERIALIZED VIEW file_count_by_language AS
SELECT
    language.id AS lang_id,
    COUNT(language.id) AS count
FROM file
INNER JOIN language
    ON file.lang_id = language.id
GROUP BY language.id;

CREATE MATERIALIZED VIEW function_count_by_language AS
SELECT
    language.id AS lang_id,
    COUNT(language.id) AS count
FROM function
INNER JOIN language
    ON function.lang_id = language.id
GROUP BY language.id;

CREATE MATERIALIZED VIEW total_code_size_in_bytes AS
SELECT SUM(characters) AS size FROM file;

CREATE UNIQUE INDEX ON table_row_count("table");
CREATE UNIQUE INDEX ON git_repo_count_by_language(lang_id);
CREATE UNIQUE INDEX ON file_count_by_language(lang_id);
CREATE UNIQUE INDEX ON function_count_by_language(lang_id);
CREATE UNIQUE INDEX ON total_code_size_in_bytes(size);
