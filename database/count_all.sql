SELECT
    (SELECT count(*) FROM "git_repo") AS "total_git_repos",
    (SELECT count(*) FROM "file") AS "total_files",
    (SELECT count(*) FROM "function") AS "total_functions"
    