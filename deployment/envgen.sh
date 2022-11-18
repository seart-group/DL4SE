#!/usr/bin/env bash

set -eu

EXPORT_PATH="${BASH_SOURCE%/*}/.env"
TEMPLATE_PATH="${BASH_SOURCE%/*}/.env.template"

envsubst < "$TEMPLATE_PATH" > "$EXPORT_PATH"