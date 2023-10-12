-- liquibase formatted sql
-- changeset dabico:5

CREATE OR REPLACE PROCEDURE
    "refresh_materialized_view"(name text)
AS $$
DECLARE
    _name text;
    _oid oid;
    _has_unique_index boolean;
BEGIN
    -- Parameter Preconditions
    IF name IS NULL
    THEN RAISE EXCEPTION SQLSTATE 'C0001'
        USING MESSAGE = 'name parameter is required';
    END IF;
    -- Check Exists
    _name := refresh_materialized_view.name;
    IF NOT EXISTS(
        SELECT FROM pg_matviews
        WHERE pg_matviews.matviewname = _name
    )
    THEN RAISE no_data_found;
    END IF;
    -- Retrieve OID
    SELECT pg_class.oid INTO _oid
    FROM pg_class
    JOIN pg_matviews ON pg_matviews.matviewname = pg_class.relname
    WHERE pg_matviews.matviewname = _name;
    -- Examine Indexes
    SELECT EXISTS(
        SELECT FROM pg_index
        WHERE pg_index.indisunique = true
          AND pg_index.indrelid = _oid
    ) INTO _has_unique_index;
    -- Execute Refresh
    EXECUTE 'REFRESH MATERIALIZED VIEW '
                || CASE WHEN _has_unique_index THEN 'CONCURRENTLY ' ELSE '' END
                || _name;
END;
$$ LANGUAGE PLpgSQL;
