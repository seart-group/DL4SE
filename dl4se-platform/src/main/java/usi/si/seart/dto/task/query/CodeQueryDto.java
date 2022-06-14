package usi.si.seart.dto.task.query;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "granularity"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FileQueryDto.class, name = "file"),
        @JsonSubTypes.Type(value = FunctionQueryDto.class, name = "function")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class CodeQueryDto {

    @NotBlank
    @JsonProperty(value = "language_name")
    String languageName;

    @NotNull
    @JsonProperty(value = "has_license")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    Boolean hasLicense = false;

    @NotNull
    @JsonProperty(value = "exclude_forks")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    Boolean excludeForks = false;

    @PositiveOrZero
    @JsonProperty(value = "min_commits")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    Long minCommits = 0L;

    @PositiveOrZero
    @JsonProperty(value = "min_contributors")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    Long minContributors = 0L;

    @PositiveOrZero
    @JsonProperty(value = "min_issues")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    Long minIssues = 0L;

    @PositiveOrZero
    @JsonProperty(value = "min_stars")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    Long minStars = 0L;

    @NotNull
    @JsonProperty(value = "include_ast")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    Boolean includeAst = true;

    @JsonProperty(value = "min_tokens")
    Long minTokens;

    @JsonProperty(value = "max_tokens")
    Long maxTokens;

    @JsonProperty(value = "min_lines")
    Long minLines;

    @JsonProperty(value = "max_lines")
    Long maxLines;

    @JsonProperty(value = "min_characters")
    Long minCharacters;

    @JsonProperty(value = "max_characters")
    Long maxCharacters;

    @NotNull
    @JsonProperty(value = "exclude_duplicates")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    Boolean excludeDuplicates = false;

    @NotNull
    @JsonProperty(value = "exclude_identical")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    Boolean excludeIdentical = false;

    @NotNull
    @JsonProperty(value = "exclude_test")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    Boolean excludeTest = false;

    @NotNull
    @JsonProperty(value = "exclude_non_ascii")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    Boolean excludeNonAscii = false;
}
