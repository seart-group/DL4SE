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

-- FOREIGN KEYS
ALTER TABLE "verification_token" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");
