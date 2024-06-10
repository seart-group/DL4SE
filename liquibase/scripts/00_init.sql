-- liquibase formatted sql
-- changeset dabico:1

CREATE SEQUENCE hibernate_sequence START 1 INCREMENT 1;

CREATE TABLE "crawl_job" (
    "id" bigint PRIMARY KEY NOT NULL,
    "checkpoint" timestamp NOT NULL,
    "job_type" text NOT NULL
);

CREATE TABLE "language" (
    "id" bigint PRIMARY KEY NOT NULL,
    "name" text NOT NULL,
    "extensions" text[] NOT NULL
);

CREATE TABLE "git_repo" (
    "id" bigint PRIMARY KEY NOT NULL,
    "name" text UNIQUE NOT NULL,
    "license" text,
    "is_fork" boolean NOT NULL,
    "commits" bigint NOT NULL,
    "contributors" bigint NOT NULL,
    "issues" bigint NOT NULL,
    "stars" bigint NOT NULL,
    "last_commit" timestamp NOT NULL,
    "last_commit_sha" text NOT NULL,
    "is_unavailable" boolean NOT NULL
);

CREATE TABLE "git_repo_language" (
    "repo_id" bigint NOT NULL,
    "lang_id" bigint NOT NULL
);

CREATE TABLE "file" (
    "id" bigint PRIMARY KEY NOT NULL,
    "repo_id" bigint NOT NULL,
    "lang_id" bigint NOT NULL,
    "path" text NOT NULL,
    "content" text NOT NULL,
    "content_hash" text NOT NULL,
    "ast" text NOT NULL,
    "ast_hash" text NOT NULL,
    "symbolic_expression" text NOT NULL,
    "total_tokens" bigint NOT NULL,
    "code_tokens" bigint NOT NULL,
    "lines" bigint NOT NULL,
    "characters" bigint NOT NULL,
    "is_test" boolean NOT NULL,
    "contains_non_ascii" boolean NOT NULL,
    "contains_error" boolean NOT NULL,
    UNIQUE (repo_id, path)
);

CREATE TABLE "function" (
    "id" bigint PRIMARY KEY NOT NULL,
    "repo_id" bigint NOT NULL,
    "lang_id" bigint NOT NULL,
    "file_id" bigint NOT NULL,
    "content" text NOT NULL,
    "content_hash" text NOT NULL,
    "ast" text NOT NULL,
    "ast_hash" text NOT NULL,
    "symbolic_expression" text NOT NULL,
    "total_tokens" bigint NOT NULL,
    "code_tokens" bigint NOT NULL,
    "lines" bigint NOT NULL,
    "characters" bigint NOT NULL,
    "is_test" boolean NOT NULL,
    "contains_non_ascii" boolean NOT NULL,
    "contains_error" boolean NOT NULL,
    "boilerplate_type" text
);

ALTER TABLE "git_repo_language" ADD FOREIGN KEY ("repo_id") REFERENCES "git_repo" ("id") ON DELETE CASCADE;
ALTER TABLE "git_repo_language" ADD FOREIGN KEY ("lang_id") REFERENCES "language" ("id");
ALTER TABLE "file" ADD FOREIGN KEY ("repo_id") REFERENCES "git_repo" ("id") ON DELETE CASCADE;
ALTER TABLE "file" ADD FOREIGN KEY ("lang_id") REFERENCES "language" ("id");
ALTER TABLE "function" ADD FOREIGN KEY ("repo_id") REFERENCES "git_repo" ("id") ON DELETE CASCADE;
ALTER TABLE "function" ADD FOREIGN KEY ("lang_id") REFERENCES "language" ("id");
ALTER TABLE "function" ADD FOREIGN KEY ("file_id") REFERENCES "file" ("id") ON DELETE CASCADE;

CREATE INDEX "git_repo_language_idx" ON "git_repo_language" (repo_id, lang_id);
CREATE INDEX "file_repo_id_idx" ON "file" (repo_id);
CREATE INDEX "file_lang_id_idx" ON "file" (lang_id);
CREATE INDEX "function_repo_id_idx" ON "function" (repo_id);
CREATE INDEX "function_lang_id_idx" ON "function" (lang_id);
CREATE INDEX "function_file_id_idx" ON "function" (file_id);
