#!/usr/bin/env bash

liquibase \
  --username="$DATABASE_USER" \
  --password="$DATABASE_PASS" \
  --url="jdbc:postgresql://$DATABASE_HOST:$DATABASE_PORT/$DATABASE_NAME" \
  --changeLogFile="liquibase/changelog.xml" \
  update