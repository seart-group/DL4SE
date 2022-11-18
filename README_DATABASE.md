# Database

## Setup

As our central database, we use [PostgreSQL](https://www.postgresql.org/). In order to test the project locally, you
must first create the necessary database and user. If you have set up the environment variables, then you can do so
easily by running the [`database/create.sh`](./database/create.sh) script.

## Migration

In order to version the project database, we make use of [Liquibase](https://www.liquibase.org/). If you want to run the
platform locally, you will need to install it. The SQL scripts present in the [`liquibase/scripts`](./liquibase/scripts)
directory represent the current schema of the project database. Some are used to initialize the schema, while others
serve to update it. Each script is tied to a database schema iteration, which is why they are all prefixed with a number.
These scripts will need to be run both on initial setup, and whenever new migration scripts are added. We have
simplified this procedure to using the [`liquibase/migrate.sh`](./liquibase/migrate.sh) script. As was the case with the
DB creation, this script also relies on a fully set-up environment. Pay attention when performing schema changes: **DO
NOT MODIFY THE EXISTING SCRIPTS**, if you want to change the schema add a new script instead.

## Cleanup

During the course of development, you may come into situations where you want to remove all created relations. We have
simplified this process by virtue of the [`database/drop.sh`](./database/drop.sh) script. To completely remove the
database and the associated role, use the [`database/clean.sh`](./database/clean.sh).

## Notes

### Homebrew Postgres Installations

If you installed Postgres via `homebrew` then for the database creation scripts to work, you need to run the following
in your terminal:

```shell
/usr/local/opt/postgres/bin/createuser -s postgres
```