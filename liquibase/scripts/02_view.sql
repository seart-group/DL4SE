CREATE MATERIALIZED VIEW table_counts AS
SELECT 'user' AS "table", COUNT(id) FROM "user"
UNION
SELECT 'task' AS "table", COUNT(id) FROM task
UNION
SELECT 'git_repo' AS "table", COUNT(id) FROM git_repo
UNION
SELECT 'file' AS "table", COUNT(id) FROM file
UNION
SELECT 'function' AS "table", COUNT(id) FROM function;

CREATE MATERIALIZED VIEW git_repos_by_language AS
SELECT l.id AS lang_id, COUNT(l.id) FROM git_repo gr
INNER JOIN git_repo_language grl ON gr.id = grl.repo_id
INNER JOIN language l on l.id = grl.lang_id
GROUP BY l.id;

CREATE MATERIALIZED VIEW files_by_language AS
SELECT l.id AS lang_id, COUNT(l.id) FROM file f
INNER JOIN language l ON f.lang_id = l.id GROUP BY l.id;

CREATE MATERIALIZED VIEW functions_by_language AS
SELECT l.id AS lang_id, COUNT(l.id) FROM function f
INNER JOIN language l ON f.lang_id = l.id GROUP BY l.id;

CREATE MATERIALIZED VIEW code_size_in_bytes AS
SELECT SUM(characters) AS size FROM file;

CREATE UNIQUE INDEX ON table_counts("table");
CREATE UNIQUE INDEX ON git_repos_by_language(lang_id);
CREATE UNIQUE INDEX ON files_by_language(lang_id);
CREATE UNIQUE INDEX ON functions_by_language(lang_id);
CREATE UNIQUE INDEX ON code_size_in_bytes(size);
