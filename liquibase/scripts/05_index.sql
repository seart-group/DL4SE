-- liquibase formatted sql
-- changeset dabico:6

CREATE INDEX "git_repo_statistics_idx" ON "git_repo" (commits, contributors, issues, stars) INCLUDE (is_fork, license);

CREATE INDEX "file_content_hash_idx" ON "file" (content_hash);
CREATE INDEX "file_ast_hash_idx" ON "file" (ast_hash);

CREATE INDEX "function_content_hash_idx" ON "function" (content_hash);
CREATE INDEX "function_ast_hash_idx" ON "function" (ast_hash);
