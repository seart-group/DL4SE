package usi.si.seart.dto.task.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FileQueryDto.class, name = "file"),
        @JsonSubTypes.Type(value = FunctionQueryDto.class, name = "function")
})
public abstract class CodeQueryDto {

    @NotBlank
    @JsonProperty(value = "language_name")
    String languageName;

    @NotNull
    @JsonProperty(value = "has_license")
    Boolean hasLicense;

    @NotNull
    @JsonProperty(value = "exclude_forks")
    Boolean excludeForks;

    @PositiveOrZero
    @JsonProperty(value = "min_commits")
    Long minCommits;

    @PositiveOrZero
    @JsonProperty(value = "min_contributors")
    Long minContributors;

    @PositiveOrZero
    @JsonProperty(value = "min_issues")
    Long minIssues;

    @PositiveOrZero
    @JsonProperty(value = "min_stars")
    Long minStars;

    @NotNull
    @JsonProperty(value = "include_ast")
    Boolean includeAst;

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
    Boolean excludeDuplicates;

    @NotNull
    @JsonProperty(value = "exclude_identical")
    Boolean excludeIdentical;

    @NotNull
    @JsonProperty(value = "exclude_non_ascii")
    Boolean excludeNonAscii;
}
