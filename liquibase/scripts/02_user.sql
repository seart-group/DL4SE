-- liquibase formatted sql
-- changeset dabico:3

CREATE TABLE "configuration" (
    "key" text PRIMARY KEY NOT NULL,
    "value" text NOT NULL
);

CREATE TABLE "user" (
    "id" bigint PRIMARY KEY NOT NULL,
    "uid" text UNIQUE NOT NULL,
    "email" text UNIQUE NOT NULL,
    "password" text NOT NULL,
    "verified" boolean NOT NULL,
    "enabled" boolean NOT NULL,
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
    "query" json NOT NULL,
    "processing" json NOT NULL,
    "status" text NOT NULL,
    "version" bigint NOT NULL,
    "checkpoint_id" bigint,
    "processed_results" bigint NOT NULL,
    "total_results" bigint NOT NULL,
    "submitted" timestamp NOT NULL,
    "started" timestamp,
    "finished" timestamp,
    "size" bigint,
    "expired" boolean NOT NULL,
    "error_stack_trace" text
);

ALTER TABLE "token" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE;
ALTER TABLE "task" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

CREATE INDEX "token_user_id_idx" ON "token" (user_id);
CREATE INDEX "task_user_id_idx" ON "task" (user_id);
CREATE INDEX "task_expired_idx" ON "task" (finished, expired) WHERE finished IS NOT NULL AND expired = false;
CREATE INDEX "file_content_hash_idx" ON "file" (content_hash);
CREATE INDEX "function_content_hash_idx" ON "function" (content_hash);
CREATE INDEX "file_ast_hash_idx" ON "file" (ast_hash);
CREATE INDEX "function_ast_hash_idx" ON "function" (ast_hash);
CREATE INDEX "git_repo_stats_idx" ON "git_repo" (commits, contributors, issues, stars) INCLUDE (is_fork, license);
