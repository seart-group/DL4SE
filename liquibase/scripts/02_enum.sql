-- liquibase formatted sql
-- changeset dabico:3

CREATE TYPE dataset AS ENUM ('CODE', 'REVIEW', 'BUG');

CREATE TYPE status AS ENUM ('QUEUED', 'EXECUTING', 'FINISHED', 'CANCELLED', 'ERROR');

CREATE TABLE dataset_progress ("dataset", "checkpoint") AS
SELECT job_type::dataset, checkpoint FROM crawl_job;

ALTER TABLE dataset_progress ADD PRIMARY KEY (dataset);

DROP TABLE crawl_job;
