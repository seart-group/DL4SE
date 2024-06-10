-- liquibase formatted sql
-- changeset dabico:7

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

ALTER TABLE "token" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE;

CREATE INDEX "token_user_id_idx" ON "token" (user_id);
