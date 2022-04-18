package usi.si.seart.converter;

import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Queries;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import usi.si.seart.model.task.CodeTask;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.task.processing.CodeProcessing;
import usi.si.seart.model.task.processing.Processing;
import usi.si.seart.model.task.query.CodeQuery;
import usi.si.seart.model.task.query.FileQuery;
import usi.si.seart.model.task.query.FunctionQuery;
import usi.si.seart.model.task.query.Query;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class TaskToQueriesConverter implements Converter<Task, Queries> {

    private final DSLContext dslCtx;

    @Override
    @NonNull
    public Queries convert(@NonNull Task source) {
        if (source instanceof CodeTask) {
            return convert((CodeTask) source);
        } else {
            throw new UnsupportedOperationException(
                    "Converter not implemented for task type: " + source.getClass().getName()
            );
        }
    }

    private Queries convert(CodeTask source) {
        Query query = source.getQuery();
        Processing processing = source.getProcessing();
        Long checkpointId = source.getCheckpointId();
        if (query instanceof CodeQuery && processing instanceof CodeProcessing) {
            return convert((CodeQuery) query, (CodeProcessing) processing, checkpointId);
        } else {
            throw new IllegalArgumentException(String.format(
                    "No SQL mapping for (query, processing) tuple types: (%s, %s)",
                    query.getClass().getName(),
                    processing.getClass().getName()
            ));
        }
    }

    private Queries convert(CodeQuery codeQuery, CodeProcessing codeProcessing, Long checkpointId) {
        org.jooq.Query resultQuery = getResultQuery(codeQuery, codeProcessing, checkpointId);
        org.jooq.Query countQuery = getCountQuery(codeQuery, codeProcessing);
        return DSL.queries(resultQuery, countQuery);
    }

    private org.jooq.Query getResultQuery(CodeQuery codeQuery, CodeProcessing codeProcessing, Long checkpointId) {
        Table<?> codeTable = getDerivedCodeTable(codeQuery, codeProcessing, checkpointId);
        Table<?> langTable = getDerivedLangTable(codeQuery);
        Table<?> repoTable = getDerivedRepoTable(codeQuery);

        Field<Long> codeTableId = codeTable.field("id", Long.class);
        Field<Long> langTableId = langTable.field("id", Long.class);
        Field<Long> repoTableId = repoTable.field("id", Long.class);
        Field<Long> codeTableLangId = codeTable.field("lang_id", Long.class);
        Field<Long> codeTableRepoId = codeTable.field("repo_id", Long.class);

        return DSL.select(codeTable.fields())
                .from(codeTable)
                .innerJoin(langTable)
                .on(codeTableLangId.equal(langTableId))
                .innerJoin(repoTable)
                .on(codeTableRepoId.equal(repoTableId))
                .orderBy(codeTableId)
                .getQuery();
    }

    private org.jooq.Query getCountQuery(CodeQuery codeQuery, CodeProcessing codeProcessing) {
        Table<?> codeTable = getDerivedCodeTable(codeQuery, codeProcessing, null);
        Table<?> langTable = getDerivedLangTable(codeQuery);
        Table<?> repoTable = getDerivedRepoTable(codeQuery);

        Field<Long> codeTableId = codeTable.field("id", Long.class);
        Field<Long> langTableId = langTable.field("id", Long.class);
        Field<Long> repoTableId = repoTable.field("id", Long.class);
        Field<Long> codeTableLangId = codeTable.field("lang_id", Long.class);
        Field<Long> codeTableRepoId = codeTable.field("repo_id", Long.class);

        return DSL.select(DSL.count(codeTableId))
                .from(codeTable)
                .innerJoin(langTable)
                .on(codeTableLangId.equal(langTableId))
                .innerJoin(repoTable)
                .on(codeTableRepoId.equal(repoTableId))
                .getQuery();
    }

    private Table<?> getDerivedCodeTable(CodeQuery codeQuery, CodeProcessing codeProcessing, Long checkpointId) {
        Field<Long> id = DSL.field("id", Long.class);
        Field<Long> repoId = DSL.field("repo_id", Long.class);
        Field<Long> langId = DSL.field("lang_id", Long.class);
        Field<String> path = DSL.field("path", String.class);
        Field<Long> fileId = DSL.field("file_id", Long.class);
        Field<String> content = DSL.field("content", String.class);
        Field<String> contentHash = DSL.field("content_hash", String.class);
        Field<String> ast = DSL.field("ast", String.class);
        Field<String> astHash = DSL.field("ast_hash", String.class);
        Field<Long> totalTokens = DSL.field("total_tokens", Long.class);
        Field<Long> codeTokens = DSL.field("code_tokens", Long.class);
        Field<Long> lines = DSL.field("lines", Long.class);
        Field<Long> characters = DSL.field("characters", Long.class);
        Field<Boolean> isTest = DSL.field("is_test", Boolean.class);
        Field<Boolean> containsNonAscii = DSL.field("contains_non_ascii", Boolean.class);
        Field<Boolean> isParsed = DSL.field("is_parsed", Boolean.class);
        Field<String> boilerplateType = DSL.field("boilerplate_type", String.class);

        List<Field<?>> fields = new ArrayList<>();
        fields.add(id);
        fields.add(repoId);
        fields.add(langId);
        if (codeQuery instanceof FileQuery) {
            fields.add(path);
            fields.add(isParsed);
        }
        if (codeQuery instanceof FunctionQuery) fields.add(fileId);
        fields.add(content);
        fields.add(contentHash);
        fields.add(ast);
        fields.add(astHash);
        fields.add(totalTokens);
        fields.add(codeTokens);
        fields.add(lines);
        fields.add(characters);
        fields.add(isTest);
        fields.add(containsNonAscii);
        if (codeQuery instanceof FunctionQuery) fields.add(boilerplateType);

        boolean excludeDuplicates = codeQuery.getExcludeDuplicates();
        boolean excludeIdentical = codeQuery.getExcludeIdentical();
        List<SelectFieldOrAsterisk> distinctColumn;
        if (excludeIdentical) {
            distinctColumn = List.of(astHash);
        } else if (excludeDuplicates) {
            distinctColumn = List.of(contentHash);
        } else {
            distinctColumn = List.of();
        }

        String tableName;
        boolean excludeUnparsable = false;
        boolean excludeBoilerplate = false;
        if (codeQuery instanceof FileQuery) {
            tableName = "file";
            excludeUnparsable = ((FileQuery) codeQuery).getExcludeUnparsable();
        } else if (codeQuery instanceof FunctionQuery) {
            tableName = "function";
            excludeBoilerplate = ((FunctionQuery) codeQuery).getExcludeBoilerplate();
        } else {
            throw new IllegalArgumentException(
                    "No SQL mapping for query type: ["+codeQuery.getClass().getName()+"]"
            );
        }

        boolean excludeTest = codeQuery.getExcludeTest();
        boolean excludeNonAscii = codeQuery.getExcludeNonAscii();
        Long minLines = codeQuery.getMinLines();
        Long maxLines = codeQuery.getMaxLines();
        Long minTokens = codeQuery.getMinTokens();
        Long maxTokens = codeQuery.getMaxTokens();
        Long minChars = codeQuery.getMinCharacters();
        Long maxChars = codeQuery.getMaxCharacters();

        boolean removeDocstring = codeProcessing.getRemoveDocstring();
        boolean removeInnerComments = codeProcessing.getRemoveInnerComments();
        Field<Long> tokens;
        if (removeDocstring && removeInnerComments) {
            tokens = codeTokens;
        } else {
            tokens = totalTokens;
        }

        Param<Long> minId = DSL.param("checkpoint_id", checkpointId);
        Param<Boolean> notTest = DSL.param("is_test", false);
        Param<Boolean> ascii = DSL.param("non_ascii", false);
        Param<Boolean> parsed = DSL.param("is_parsed", true);

        List<Condition> conditions = new ArrayList<>();
        conditions.add(getRangeCondition(lines, minLines, maxLines));
        conditions.add(getRangeCondition(characters, minChars, maxChars));
        conditions.add(getRangeCondition(tokens, minTokens, maxTokens));
        if (excludeTest) conditions.add(isTest.equal(notTest));
        if (excludeNonAscii) conditions.add(containsNonAscii.equal(ascii));
        if (excludeUnparsable) conditions.add(isParsed.equal(parsed));
        if (excludeBoilerplate) conditions.add(boilerplateType.isNull());
        if (minId.getValue() != null) conditions.add(id.greaterThan(minId));

        return dslCtx.select(fields)
                .distinctOn(distinctColumn)
                .from(tableName)
                .where(conditions)
                .asTable("f");
    }

    private Table<?> getDerivedLangTable(CodeQuery codeQuery) {
        Field<Long> id = DSL.field("id", Long.class);
        Param<Long> idParam = DSL.param("lang_id", codeQuery.getLanguage().getId());
        return dslCtx.select(id)
                .from("language")
                .where(id.equal(idParam))
                .asTable("l");
    }

    private Table<?> getDerivedRepoTable(Query query) {
        boolean hasLicense = query.getHasLicense();
        boolean excludeForks = query.getExcludeForks();

        Param<Long> minCommits = DSL.param("commits_min", query.getMinCommits());
        Param<Long> minContributors = DSL.param("contributors_min", query.getMinContributors());
        Param<Long> minIssues = DSL.param("issues_min", query.getMinIssues());
        Param<Long> minStars = DSL.param("stars_min", query.getMinStars());
        Param<Boolean> notFork = DSL.param("fork", false);

        Field<Long> id = DSL.field("id", Long.class);
        Field<String> license = DSL.field("license", String.class);
        Field<Boolean> isFork = DSL.field("is_fork", Boolean.class);
        Field<Long> commits = DSL.field("commits", Long.class);
        Field<Long> contributors = DSL.field("contributors", Long.class);
        Field<Long> issues = DSL.field("issues", Long.class);
        Field<Long> stars = DSL.field("stars", Long.class);

        List<Condition> conditions = new ArrayList<>();
        conditions.add(commits.greaterOrEqual(minCommits));
        conditions.add(contributors.greaterOrEqual(minContributors));
        conditions.add(issues.greaterOrEqual(minIssues));
        conditions.add(stars.greaterOrEqual(minStars));
        if (excludeForks) conditions.add(isFork.equal(notFork));
        if (hasLicense) conditions.add(license.isNotNull());

        return dslCtx.select(id)
                .from("git_repo")
                .where(conditions)
                .asTable("gr");
    }

    private static <T> Condition getRangeCondition(Field<T> field, T lower, T upper) {
        Param<T> lowerParam;
        Param<T> upperParam;
        if (lower != null) {
            lowerParam = DSL.param(field.getName()+"_min", lower);
            if (upper != null) {
                upperParam = DSL.param(field.getName()+"_max", upper);
                return field.between(lowerParam, upperParam);
            } else {
                return field.greaterOrEqual(lowerParam);
            }
        } else {
            if (upper != null) {
                upperParam = DSL.param(field.getName()+"_max", upper);
                return field.lessOrEqual(upperParam);
            } else {
                return DSL.noCondition();
            }
        }
    }
}
