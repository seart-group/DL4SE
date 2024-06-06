-- liquibase formatted sql
-- changeset dabico:8

CREATE TABLE "configuration" (
    "key" text PRIMARY KEY NOT NULL,
    "value" text NOT NULL
);

CREATE TABLE "task" (
    "id" bigint PRIMARY KEY NOT NULL,
    "uuid" uuid UNIQUE NOT NULL,
    "dataset" dataset NOT NULL,
    "user_id" bigint NOT NULL,
    "query" json NOT NULL,
    "processing" json NOT NULL,
    "status" status NOT NULL,
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

ALTER TABLE "task" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

CREATE INDEX "task_user_id_idx" ON "task" (user_id);
CREATE INDEX "task_expired_idx" ON "task" (finished, expired) WHERE finished IS NOT NULL AND expired = false;
