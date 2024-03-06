-- liquibase formatted sql
-- changeset dabico:4

CREATE TABLE IF NOT EXISTS tree_sitter_version (
    "id" bigint PRIMARY KEY NOT NULL,
    "sha" text NOT NULL,
    "tag" text NOT NULL,
    UNIQUE ("sha", "tag")
);

ALTER TABLE "file" ADD COLUMN IF NOT EXISTS "version_id" bigint;
ALTER TABLE "file" ALTER COLUMN "version_id" SET NOT NULL;
ALTER TABLE "file" ADD FOREIGN KEY ("version_id") REFERENCES "tree_sitter_version" ("id");

CREATE INDEX "file_version_id_idx" ON "file" ("version_id");
