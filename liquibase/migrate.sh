#!/usr/bin/env bash

liquibase --url="jdbc:postgresql://$DATABASE_HOST:$DATABASE_PORT/$DATABASE_NAME?user=$DATABASE_USER&password=$DATABASE_PASS" --changeLogFile="liquibase/changelog.xml" --hub-mode=off update