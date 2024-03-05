-- liquibase formatted sql
-- changeset dabico:4

CREATE TABLE IF NOT EXISTS metadata (
    "id" bigint PRIMARY KEY NOT NULL,
    "binding_version" text NOT NULL,
    "binding_git_sha" text NOT NULL,
    "binding_git_url" text NOT NULL,
    "binding_git_tag" text NOT NULL,
    "api_version" int NOT NULL,
    "api_git_sha" text NOT NULL,
    "api_git_url" text NOT NULL,
    "api_git_tag" text NOT NULL,
    "language_version" int NOT NULL,
    "language_git_sha" text NOT NULL,
    "language_git_url" text NOT NULL,
    "language_git_tag" text NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS metadata_sha_idx ON metadata (
    "binding_git_sha", "api_git_sha", "language_git_sha"
);

ALTER TABLE "file" ADD COLUMN IF NOT EXISTS meta_id bigint;
ALTER TABLE "file" ALTER COLUMN meta_id SET NOT NULL;
ALTER TABLE "file" ADD FOREIGN KEY (meta_id) REFERENCES "metadata" ("id");
