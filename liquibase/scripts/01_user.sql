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

CREATE TABLE "verification_token" (
    "id" bigint PRIMARY KEY NOT NULL,
    "value" text UNIQUE NOT NULL,
    "user_id" bigint NOT NULL,
    "expires" timestamp NOT NULL
);

CREATE TABLE "task" (
    "id" bigint PRIMARY KEY NOT NULL,
    "uuid" uuid UNIQUE NOT NULL,
    "user_id" bigint NOT NULL,
    "status" text NOT NULL,
    "export_path" text NOT NULL,
    "checkpoint_id" bigint,
    "processed_results" bigint,
    "total_results" bigint,
    "submitted" timestamp NOT NULL,
    "started" timestamp,
    "finished" timestamp
);

CREATE TABLE "file_query" (
    "id" bigint PRIMARY KEY NOT NULL,
    "task_id" bigint NOT NULL,
    "lang_id" bigint NOT NULL,
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
    "exclude_non_ascii" boolean NOT NULL,
    "exclude_unparsable" boolean NOT NULL
);

CREATE TABLE "function_query" (
    "id" bigint PRIMARY KEY NOT NULL,
    "task_id" bigint NOT NULL,
    "lang_id" bigint NOT NULL,
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
    "exclude_non_ascii" boolean NOT NULL,
    "exclude_boilerplate" boolean NOT NULL
);

CREATE TABLE "code_processing" (
    "id" bigint PRIMARY KEY NOT NULL,
    "task_id" bigint NOT NULL,
    "mask_token" text NOT NULL,
    "mask_percentage" integer NOT NULL,
    "mask_contiguous_only" boolean NOT NULL,
    "idioms" text[] NOT NULL
);

-- FOREIGN KEYS
ALTER TABLE "verification_token" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE;
ALTER TABLE "task" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");
ALTER TABLE "file_query" ADD FOREIGN KEY ("task_id") REFERENCES "task" ("id") ON DELETE CASCADE;
ALTER TABLE "file_query" ADD FOREIGN KEY ("lang_id") REFERENCES "language" ("id");
ALTER TABLE "function_query" ADD FOREIGN KEY ("task_id") REFERENCES "task" ("id") ON DELETE CASCADE;
ALTER TABLE "function_query" ADD FOREIGN KEY ("lang_id") REFERENCES "language" ("id");
ALTER TABLE "code_processing" ADD FOREIGN KEY ("task_id") REFERENCES "task" ("id") ON DELETE CASCADE;

-- INDEXES
CREATE INDEX "task_user_id_idx" ON "task" (user_id);
CREATE INDEX "file_content_hash_idx" ON "file" (content_hash);
CREATE INDEX "function_content_hash_idx" ON "function" (content_hash);
CREATE INDEX "file_ast_hash_idx" ON "file" (ast_hash);
CREATE INDEX "function_ast_hash_idx" ON "function" (ast_hash);
CREATE INDEX "git_repo_stats_idx" ON "git_repo" (commits, contributors, issues, stars, is_fork, license);
