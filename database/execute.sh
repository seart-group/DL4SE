#!/usr/bin/env bash

SCRIPT_ABS_PATH="${BASH_SOURCE%/*}/$1" && \
test -f "$SCRIPT_ABS_PATH"
psql -d "$DATABASE_NAME" -U "$DATABASE_USER" -c "$(cat "$SCRIPT_ABS_PATH")"
