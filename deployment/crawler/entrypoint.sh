#!/usr/bin/env sh

while : ;
do
  java \
  -DDATABASE_HOST="$DATABASE_HOST" \
  -DDATABASE_NAME="$DATABASE_NAME" \
  -DDATABASE_PASS="$DATABASE_PASS" \
  -DDATABASE_PORT="$DATABASE_PORT" \
  -DDATABASE_USER="$DATABASE_USER" \
  -Dfile.encoding=UTF-8 \
  -jar crawler.jar
  EXIT_CODE=$?
  if [ $EXIT_CODE -eq 0 ] ;\
    then
      echo "Restarting in 6 hours from now..."
      sleep 21600
    else
      echo "An error has occurred, exiting..."
      exit $EXIT_CODE
  fi
done