#!/usr/bin/env bash

GEN_SCRIPT_PATH="${BASH_SOURCE%/*}/gen_drop_cmds.sql"
SCRIPT=$(psql -t -d "$DATABASE_NAME" -U "$DATABASE_USER" -c "$(cat "$GEN_SCRIPT_PATH")")
psql -d "$DATABASE_NAME" -U "$DATABASE_USER" -c "$SCRIPT"
