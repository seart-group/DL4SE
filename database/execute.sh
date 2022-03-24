#!/usr/bin/env bash

psql -d "$DATABASE_NAME" -U "$DATABASE_USER" -c "$(echo "${BASH_SOURCE%/*}/$1" | xargs -L 1 cat)"
