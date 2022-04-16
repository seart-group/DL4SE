-- TABLES
CREATE TABLE "user" (
    "id" bigint PRIMARY KEY NOT NULL,
    "email" text UNIQUE NOT NULL,
    "password" text NOT NULL,
    "verified" boolean NOT NULL,
    "organisation" text NOT NULL,
    "role" text NOT NULL,
    "registered" timestamp NOT NULL
);

CREATE TABLE "token" (
    "id" bigint PRIMARY KEY NOT NULL,
    "type" text NOT NULL,
    "value" text UNIQUE NOT NULL,
    "user_id" bigint NOT NULL,
    "expires" timestamp NOT NULL
);

CREATE TABLE "task" (
    "id" bigint PRIMARY KEY NOT NULL,
    "uuid" uuid UNIQUE NOT NULL,
    "dataset" text NOT NULL,
    "user_id" bigint NOT NULL,
    "status" text NOT NULL,
    "export_path" text NOT NULL,
    "checkpoint_id" bigint,
    "processed_results" bigint NOT NULL,
    "total_results" bigint NOT NULL,
    "submitted" timestamp NOT NULL,
    "started" timestamp,
    "finished" timestamp
);

CREATE TABLE "query" (
    "id" bigint PRIMARY KEY NOT NULL,
    "task_id" bigint NOT NULL,
    "lang_id" bigint NOT NULL,
    "type" text NOT NULL,
    "has_license" boolean NOT NULL,
    "exclude_forks" boolean NOT NULL,
    "min_commits" bigint NOT NULL,
    "min_contributors" bigint NOT NULL,
    "min_issues" bigint NOT NULL,
    "min_stars" bigint NOT NULL,
    "include_ast" boolean NOT NULL,
    "min_tokens" bigint,
    "max_tokens" bigint,
    "min_lines" bigint,
    "max_lines" bigint,
    "min_characters" bigint,
    "max_characters" bigint,
    "exclude_duplicates" boolean NOT NULL,
    "exclude_identical" boolean NOT NULL,
    "exclude_test" boolean NOT NULL,
    "exclude_non_ascii" boolean NOT NULL,
    "exclude_unparsable" boolean,
    "exclude_boilerplate" boolean
);

CREATE TABLE "processing" (
    "id" bigint PRIMARY KEY NOT NULL,
    "task_id" bigint NOT NULL,
    "type" text NOT NULL,
    "remove_docstring" boolean NOT NULL,
    "remove_inner_comments" boolean NOT NULL,
    "mask_token" text NOT NULL,
    "mask_percentage" integer NOT NULL,
    "mask_contiguous_only" boolean NOT NULL,
    "idioms" text[] NOT NULL
);

-- FOREIGN KEYS
ALTER TABLE "token" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE;
ALTER TABLE "task" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");
ALTER TABLE "query" ADD FOREIGN KEY ("task_id") REFERENCES "task" ("id") ON DELETE CASCADE;
ALTER TABLE "query" ADD FOREIGN KEY ("lang_id") REFERENCES "language" ("id");
ALTER TABLE "processing" ADD FOREIGN KEY ("task_id") REFERENCES "task" ("id") ON DELETE CASCADE;

-- INDEXES
CREATE INDEX "token_user_id_idx" ON "token" (user_id);
CREATE INDEX "task_user_id_idx" ON "task" (user_id);
CREATE INDEX "query_task_id_idx" ON "query" (task_id);
CREATE INDEX "query_lang_id_idx" ON "query" (lang_id);
CREATE INDEX "processing_task_id_idx" ON "processing" (task_id);
CREATE INDEX "file_content_hash_idx" ON "file" (content_hash);
CREATE INDEX "function_content_hash_idx" ON "function" (content_hash);
CREATE INDEX "file_ast_hash_idx" ON "file" (ast_hash);
CREATE INDEX "function_ast_hash_idx" ON "function" (ast_hash);
CREATE INDEX "git_repo_stats_idx" ON "git_repo" (commits, contributors, issues, stars) include (is_fork, license);
