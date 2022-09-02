# Database

## Setup

In order to test the project locally, you must first create the necessary database and user. You can do so using the
`database/create.sh` script.

## Migration

Test database migrations locally using the `liquibase/migrate.sh` script.

## Cleanup

To drop all the database relations, use `database/drop.sh`. To completely drop the database and remove the created role,
use `database/clean.sh`.

## Notes

### Homebrew Postgres Installations

If you installed Postgres via `homebrew` then for the database creation scripts to work, you need to run the following
in your terminal:
```shell
/usr/local/opt/postgres/bin/createuser -s postgres
```