# Database

## Relational Structure

You can view the current schema rendition [here](https://dbdiagram.io/d/6202862685022f4ee55b0274). The following
sections will provide additional information on the purpose of each relation.

### `crawl_job`

Used to keep track of the mining progress for each software task. It maps the task name to a checkpoint timestamp, which
represents the date up to which information was mined for that task.

### `language`

Used to represent a programming language which the platform supports. Each `language` is essentially a mapping between a
name and a list of file extensions that belong to it. It is part of a many-to-many relationship with `git_repo`,
implemented via the `git_repo_language` table.

### `git_repo`

Used to represent a GitHub project. This is a "root relation" for all other forms of data from different datasets.
Information housed within it is a subset of the information found on [GitHub Search](https://github.com/seart-group/ghs)
. It is part of a many-to-many relationship with `language`, implemented via the `git_repo_language` table, as a project
can house files written in different languages. While we do not automatically delete rows related to repositories that
are no longer publicly available, we do flag these entries in the `is_delted` column.

### `file`

Used to represent a file of a specific project, written in a certain language. Each file is uniquely identified by the
project it belongs to, and its path in said project. We store the file contents as raw strings[^1], as well as the AST
representation as an XML string. To speed up duplicate checks, we pre-compute the SHA-256 hash over the contents and the
AST. Matching content hashes imply files with duplicate contents, which matching AST hashes imply structural similarity.
We also count the number of tokens in a file, with `code_tokens` being the number of non-space and non-comment tokens.
To keep track of parsing errors encountered for specific files, we introduced the `is_parsed` flag.

[^1]: Raw in the sense that we do not modify the file contents in any way. The underlying parsers we use may or may not
perform space normalizations file contents as defined by their implementation.

### `function`

Used to represent a function found within a file: a self-contained block of code used to perform a certain task(s).
Although a `function` references exactly one `file`, we negate the need for joins by also introducing relations to
`git_repo` and `language`. Here we exercise a bit of redundancy, as segments of file contents are also found in their
functions. Likewise, any file with a `is_test` flag set to `true` implies all associated functions also have the flag
set to `true`. A column unique to function is `boilerplate_type`, which is an uppercase name of the boilerplate type
family said function belongs to.

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