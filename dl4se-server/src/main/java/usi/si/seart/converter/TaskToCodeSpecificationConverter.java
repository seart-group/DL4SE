package usi.si.seart.converter;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import usi.si.seart.model.GitRepo_;
import usi.si.seart.model.Language_;
import usi.si.seart.model.code.Boilerplate;
import usi.si.seart.model.code.Code;
import usi.si.seart.model.code.Code_;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;
import usi.si.seart.model.code.Function_;
import usi.si.seart.model.job.Job;
import usi.si.seart.model.task.Task;

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
public class TaskToCodeSpecificationConverter implements Converter<Task, Specification<Code>> {

    protected static final java.util.function.Predicate<Long> isPositive = value -> value > 0;

    @Override
    @NonNull
    public Specification<Code> convert(@NonNull Task source) {
        Job dataset = source.getDataset();
        JsonNode query = source.getQuery();
        JsonNode processing = source.getProcessing();
        Assert.isTrue(Job.CODE.equals(dataset), "Can not convert this type of dataset: " + dataset);
        Assert.isTrue(query.isObject(), "Query must be a JSON object!");
        Assert.isTrue(processing.isObject(), "Processing must be a JSON object!");
        Assert.isTrue(query.has("granularity"), "Query must have 'granularity' defined!");
        String granularity = query.get("granularity").asText();
        return convert(query, processing, granularity);
    }

    private Specification<Code> convert(
            @NonNull JsonNode query, @NonNull JsonNode processing, @NonNull String granularity
    ) {
        switch (granularity) {
            case "file":
                return convert(query, processing, File.class);
            case "function":
                return convert(query, processing, Function.class);
            default:
                throw new IllegalArgumentException("Converter not implemented for granularity: " + granularity);
        }
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
                    .filter(isPositive)
                    .ifPresent(value -> {
                        Path<Long> path = root.join(Code_.repo).get(GitRepo_.commits);
                        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Minimum contributors predicate
            Optional.ofNullable(query.get("min_contributors"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(isPositive)
                    .ifPresent(value -> {
                        Path<Long> path = root.join(Code_.repo).get(GitRepo_.contributors);
                        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Minimum issues predicate
            Optional.ofNullable(query.get("min_issues"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(isPositive)
                    .ifPresent(value -> {
                        Path<Long> path = root.join(Code_.repo).get(GitRepo_.issues);
                        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Minimum stars predicate
            Optional.ofNullable(query.get("min_stars"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(isPositive)
                    .ifPresent(value -> {
                        Path<Long> path = root.join(Code_.repo).get(GitRepo_.stars);
                        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Minimum characters predicate
            Optional.ofNullable(query.get("min_characters"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(isPositive)
                    .ifPresent(value -> {
                        Path<Long> path = root.get(Code_.characters);
                        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Maximum characters predicate
            Optional.ofNullable(query.get("max_characters"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(isPositive)
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
                    .filter(isPositive)
                    .ifPresent(value -> {
                        Path<Long> path = root.get(attribute);
                        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Maximum tokens predicate
            Optional.ofNullable(query.get("max_tokens"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(isPositive)
                    .ifPresent(value -> {
                        Path<Long> path = root.get(attribute);
                        Predicate predicate = criteriaBuilder.lessThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Minimum lines predicate
            Optional.ofNullable(query.get("min_lines"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(isPositive)
                    .ifPresent(value -> {
                        Path<Long> path = root.get(Code_.lines);
                        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(path, value);
                        predicates.add(predicate);
                    });

            // Maximum lines predicate
            Optional.ofNullable(query.get("max_lines"))
                    .map(JsonNode::numberValue)
                    .map(Number::longValue)
                    .filter(isPositive)
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
                Path<Long> path = root.get(Code_.id);
                SingularAttribute<Code, String> distinct = excludeIdentical
                        ? Code_.astHash
                        : Code_.contentHash;
                Subquery<Long> subselect = criteriaQuery.subquery(Long.class);
                Root<? extends Code> subselectRoot = subselect.from(granularity);
                subselect.select(criteriaBuilder.min(subselectRoot.get(Code_.id)));
                subselect.groupBy(subselectRoot.get(distinct));
                Expression<Long> expression = subselect.getSelection();
                Predicate predicate = path.in(expression);
                predicates.add(predicate);
            }

            // Exclude test instances
            Optional.ofNullable(query.get("exclude_test"))
                    .map(JsonNode::asBoolean)
                    .filter(Boolean::booleanValue)
                    .ifPresent(ignored -> {
                        Path<Boolean> path = root.get(Code_.isTest);
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
}
