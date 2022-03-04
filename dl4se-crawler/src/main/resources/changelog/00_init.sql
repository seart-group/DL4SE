-- ID GENERATION SEQUENCE
CREATE SEQUENCE hibernate_sequence START 1 INCREMENT 1;

-- TABLES
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
    "last_commit_sha" text NOT NULL,
    "last_update" timestamp NOT NULL,
    "is_deleted" boolean NOT NULL
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
    "is_parsed" boolean NOT NULL,
    "content" text NOT NULL,
    "content_hash" text NOT NULL,
    "ast" text,
    "ast_hash" text,
    "total_tokens" bigint,
    "code_tokens" bigint,
    "lines" bigint NOT NULL,
    "characters" bigint NOT NULL,
    "is_test" boolean NOT NULL,
    "contains_non_ascii" boolean NOT NULL
);

CREATE TABLE "function" (
    "id" bigint PRIMARY KEY NOT NULL,
    "repo_id" bigint NOT NULL,
    "lang_id" bigint NOT NULL,
    "file_id" bigint NOT NULL,
    "content" text NOT NULL,
    "content_hash" text NOT NULL,
    "ast" text,
    "ast_hash" text,
    "total_tokens" bigint,
    "code_tokens" bigint,
    "lines" bigint NOT NULL,
    "characters" bigint NOT NULL,
    "is_test" boolean NOT NULL,
    "contains_non_ascii" boolean NOT NULL,
    "boilerplate_type" text
);

-- FOREIGN KEYS
ALTER TABLE "git_repo_language" ADD FOREIGN KEY ("repo_id") REFERENCES "git_repo" ("id");
ALTER TABLE "git_repo_language" ADD FOREIGN KEY ("lang_id") REFERENCES "language" ("id");
ALTER TABLE "file" ADD FOREIGN KEY ("repo_id") REFERENCES "git_repo" ("id");
ALTER TABLE "file" ADD FOREIGN KEY ("lang_id") REFERENCES "language" ("id");
ALTER TABLE "function" ADD FOREIGN KEY ("repo_id") REFERENCES "git_repo" ("id");
ALTER TABLE "function" ADD FOREIGN KEY ("lang_id") REFERENCES "language" ("id");
ALTER TABLE "function" ADD FOREIGN KEY ("file_id") references "file" ("id");

-- INDEXES
CREATE INDEX "git_repo_language_idx" ON "git_repo_language" (repo_id, lang_id);
CREATE INDEX "file_repo_id_idx" ON "file" (repo_id);
CREATE INDEX "file_lang_id_idx" ON "file" (lang_id);
CREATE INDEX "function_repo_id_idx" ON "function" (repo_id);
CREATE INDEX "function_lang_id_idx" ON "function" (lang_id);

-- ADD LANGUAGES
INSERT INTO language(id, name, extensions)
VALUES
    (nextval('hibernate_sequence'), 'Java', '{java}');
