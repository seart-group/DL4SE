package ch.usi.si.seart.server.converter;

import ch.usi.si.seart.model.GitRepo_;
import ch.usi.si.seart.model.Language_;
import ch.usi.si.seart.model.code.Boilerplate;
import ch.usi.si.seart.model.code.Code;
import ch.usi.si.seart.model.code.Code_;
import ch.usi.si.seart.model.code.File_;
import ch.usi.si.seart.model.code.Function;
import ch.usi.si.seart.model.code.Function_;
import ch.usi.si.seart.model.task.Task;
import ch.usi.si.seart.views.code.HashDistinct;
import ch.usi.si.seart.views.code.HashDistinct_;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TaskToCodeSpecificationConverter extends TaskConverter<Specification<Code>> {

    private static final java.util.function.Predicate<Long> IS_POSITIVE = value -> value > 0;

    @Override
    protected void validate(Task source) {
        super.validate(source);
        JsonNode query = source.getQuery();
        Assert.isTrue(query.has("granularity"), "Query must have 'granularity' defined!");
    }

    @Override
    protected Specification<Code> convertInternal(Task source) {
        JsonNode query = source.getQuery();
        String granularity = query.get("granularity").asText();
        JsonNode processing = source.getProcessing();
        return convert(query, processing, granularity);
    }

    private Specification<Code> convert(
            @NonNull JsonNode query, @NonNull JsonNode processing, @NonNull String granularity
    ) {
        Class<? extends Code> granularityType = getCodeClass(granularity);
        return convert(query, processing, granularityType);
    }

    private Specification<Code> convert(
            @NonNull JsonNode query, @NonNull JsonNode processing, Class<? extends Code> granularity
    ) {
        return (supertypeRoot, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Root<? extends Code> root = criteriaBuilder.treat(supertypeRoot, granularity);

            // Entity type predicate
            Predicate entityType = criteriaBuilder.equal(root.type(), criteriaBuilder.literal(granularity));
            predicates.add(entityType);

            // Language predicate
            Optional.ofNullable(query.get("language_name"))
                    .map(JsonNode::asText)
                    .filter(StringUtils::hasText)
                    .ifPresent(value -> {
                        Path<String> path = root.join(Code_.language).get(Language_.name);
                        Predicate predicate = criteriaBuilder.equal(path, value);
                        predicates.add(predicate);
                    });

            // Has license check
            Optional.ofNullable(query.get("has_license"))
                    .map(JsonNode::asBoolean)
                    .filter(Boolean::booleanValue)
                    .ifPresent(ignored -> {
                        Path<String> path = root.join(Code_.repo).get(GitRepo_.license);
                        Predicate predicate = criteriaBuilder.isNotNull(path);
                        predicates.add(predicate);
                    });

            // Exclude fork predicate
            Optional.ofNullable(query.get("exclude_forks"))
                    .map(JsonNode::asBoolean)
                    .filter(Boolean::booleanValue)
                    .ifPresent(ignored -> {
                        Path<Boolean> path = root.join(Code_.repo).get(GitRepo_.isFork);
                        Predicate predicate = criteriaBuilder.isFalse(path);
                        predicates.add(predicate);
                    });

            // Minimum commits predicate
            Optional.ofNullable(query.get("min_commits"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(IS_POSITIVE)
                    .ifPresent(value -> {
                        Path<Long> path = root.join(Code_.repo).get(GitRepo_.commits);
                        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Minimum contributors predicate
            Optional.ofNullable(query.get("min_contributors"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(IS_POSITIVE)
                    .ifPresent(value -> {
                        Path<Long> path = root.join(Code_.repo).get(GitRepo_.contributors);
                        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Minimum issues predicate
            Optional.ofNullable(query.get("min_issues"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(IS_POSITIVE)
                    .ifPresent(value -> {
                        Path<Long> path = root.join(Code_.repo).get(GitRepo_.issues);
                        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Minimum stars predicate
            Optional.ofNullable(query.get("min_stars"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(IS_POSITIVE)
                    .ifPresent(value -> {
                        Path<Long> path = root.join(Code_.repo).get(GitRepo_.stars);
                        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Minimum characters predicate
            Optional.ofNullable(query.get("min_characters"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(IS_POSITIVE)
                    .ifPresent(value -> {
                        Path<Long> path = root.get(Code_.characters);
                        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Maximum characters predicate
            Optional.ofNullable(query.get("max_characters"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(IS_POSITIVE)
                    .ifPresent(value -> {
                        Path<Long> path = root.get(Code_.characters);
                        Predicate predicate = criteriaBuilder.lessThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            boolean removeInnerComments = Optional.ofNullable(processing.get("remove_inner_comments"))
                    .map(JsonNode::asBoolean)
                    .orElse(false);
            boolean removeDocstrings = Optional.ofNullable(processing.get("remove_docstring"))
                    .map(JsonNode::asBoolean)
                    .orElse(false);
            SingularAttribute<Code, Long> attribute = removeInnerComments && removeDocstrings
                    ? Code_.codeTokens
                    : Code_.totalTokens;

            // Minimum tokens predicate
            Optional.ofNullable(query.get("min_tokens"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(IS_POSITIVE)
                    .ifPresent(value -> {
                        Path<Long> path = root.get(attribute);
                        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Maximum tokens predicate
            Optional.ofNullable(query.get("max_tokens"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(IS_POSITIVE)
                    .ifPresent(value -> {
                        Path<Long> path = root.get(attribute);
                        Predicate predicate = criteriaBuilder.lessThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Minimum lines predicate
            Optional.ofNullable(query.get("min_lines"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(IS_POSITIVE)
                    .ifPresent(value -> {
                        Path<Long> path = root.get(Code_.lines);
                        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Maximum lines predicate
            Optional.ofNullable(query.get("max_lines"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(IS_POSITIVE)
                    .ifPresent(value -> {
                        Path<Long> path = root.get(Code_.lines);
                        Predicate predicate = criteriaBuilder.lessThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Duplicates predicate
            boolean excludeIdentical = Optional.ofNullable(query.get("exclude_identical"))
                    .map(JsonNode::asBoolean)
                    .orElse(false);
            boolean excludeDuplicates = Optional.ofNullable(query.get("exclude_duplicates"))
                    .map(JsonNode::asBoolean)
                    .orElse(false);
            if (excludeIdentical || excludeDuplicates) {
                Class<? extends HashDistinct> distinct = getViewClass(granularity.getSimpleName(), excludeIdentical);
                Subquery<Boolean> subquery = criteriaQuery.subquery(Boolean.class);
                Root<? extends HashDistinct> subroot = subquery.from(distinct);
                Path<Long> codePath = root.get(Code_.id);
                Path<Long> hashPath = subroot.get(HashDistinct_.id);
                Expression<Boolean> ignored = criteriaBuilder.literal(true);
                Predicate equal = criteriaBuilder.equal(codePath, hashPath);
                subquery.select(ignored).where(equal);
                Predicate predicate = criteriaBuilder.exists(subquery);
                predicates.add(predicate);
            }

            // Exclude test instances
            Optional.ofNullable(query.get("exclude_test"))
                    .map(JsonNode::asBoolean)
                    .filter(Boolean::booleanValue)
                    .ifPresent(ignored -> {
                        Path<Boolean> path = Function.class.equals(granularity)
                                ? root.join(Function_.FILE).get(File_.IS_TEST)
                                : root.get(File_.IS_TEST);
                        Predicate predicate = criteriaBuilder.isFalse(path);
                        predicates.add(predicate);
                    });

            // Exclude instances containing non-ASCII characters
            Optional.ofNullable(query.get("exclude_non_ascii"))
                    .map(JsonNode::asBoolean)
                    .filter(Boolean::booleanValue)
                    .ifPresent(ignored -> {
                        Path<Boolean> path = root.get(Code_.containsNonAscii);
                        Predicate predicate = criteriaBuilder.isFalse(path);
                        predicates.add(predicate);
                    });

            // Exclude instances containing errors
            Optional.ofNullable(query.get("exclude_errors"))
                    .map(JsonNode::asBoolean)
                    .filter(Boolean::booleanValue)
                    .ifPresent(ignored -> {
                        Path<Boolean> path = root.get(Code_.containsError);
                        Predicate predicate = criteriaBuilder.isFalse(path);
                        predicates.add(predicate);
                    });

            // Exclude boilerplate instances
            Optional.ofNullable(query.get("exclude_boilerplate"))
                    .map(JsonNode::asBoolean)
                    .filter(value -> value && Function.class.equals(granularity))
                    .ifPresent(ignored -> {
                        Path<Boilerplate> path = root.get(Function_.BOILERPLATE_TYPE);
                        Predicate predicate = criteriaBuilder.isNull(path);
                        predicates.add(predicate);
                    });

            criteriaQuery.distinct(true);
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Code> getCodeClass(String granularity) {
        try {
            String pkg = Code.class.getPackageName();
            String capitalized = StringUtils.capitalize(granularity);
            String name = String.format("%s.%s", pkg, capitalized);
            return (Class<? extends Code>) Class.forName(name);
        } catch (ClassNotFoundException ignored) {
            throw new IllegalArgumentException("Converter not implemented for granularity: " + granularity);
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends HashDistinct> getViewClass(String granularity, boolean identical) {
        try {
            String pkg = HashDistinct.class.getPackageName();
            String category = identical ? "Ast" : "Content";
            String name = String.format("%s.%s%sHashDistinct", pkg, granularity, category);
            return (Class<? extends HashDistinct>) Class.forName(name);
        } catch (ClassNotFoundException ignored) {
            throw new IllegalArgumentException("No hash view for granularity: " + granularity);
        }
    }
}
