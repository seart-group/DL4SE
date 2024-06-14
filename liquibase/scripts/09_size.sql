-- liquibase formatted sql
-- changeset dabico:10

DROP MATERIALIZED VIEW IF EXISTS total_code_size_in_bytes;
CREATE MATERIALIZED VIEW total_code_size_in_bytes AS
SELECT SUM(LENGTH(content)) AS size FROM file;

CREATE MATERIALIZED VIEW total_code_lines AS
SELECT SUM(line_count) AS lines
FROM (
    SELECT COUNT(*) AS line_count
    FROM (
        SELECT regexp_split_to_table(content, E'\n') AS line
        FROM file
    ) AS split_lines
    WHERE line ~ '\S'
) AS line_counts;

CREATE UNIQUE INDEX ON total_code_size_in_bytes(size);
CREATE UNIQUE INDEX ON total_code_lines(lines);
