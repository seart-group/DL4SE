#!/usr/bin/env bash

psql -U postgres -c "
SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = '$DATABASE_NAME';" \
&& \
psql -U postgres -c "DROP DATABASE $DATABASE_NAME" \
&& \
psql -U postgres -c "DROP ROLE $DATABASE_USER"
