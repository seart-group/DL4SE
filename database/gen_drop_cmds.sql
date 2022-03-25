SELECT 'DROP TABLE IF EXISTS "' || tablename || '" CASCADE;' AS command
FROM pg_tables
WHERE schemaname = 'public' UNION
SELECT 'DROP SEQUENCE IF EXISTS "' || sequencename || '" CASCADE;' AS command
FROM pg_sequences
WHERE schemaname = 'public';
