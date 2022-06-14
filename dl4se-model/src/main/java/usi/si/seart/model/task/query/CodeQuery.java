package usi.si.seart.model.task.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import usi.si.seart.model.Language;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class CodeQuery extends Query {

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "lang_id")
    @JsonUnwrapped(prefix = "language_")
    Language language;

    @NotNull
    @Column(name = "include_ast")
    @JsonProperty(value = "include_ast")
    Boolean includeAst;

    @Column(name = "min_tokens")
    @JsonProperty(value = "min_tokens")
    Long minTokens;

    @Column(name = "max_tokens")
    @JsonProperty(value = "max_tokens")
    Long maxTokens;

    @Column(name = "min_lines")
    @JsonProperty(value = "min_lines")
    Long minLines;

    @Column(name = "max_lines")
    @JsonProperty(value = "max_lines")
    Long maxLines;

    @Column(name = "min_characters")
    @JsonProperty(value = "min_characters")
    Long minCharacters;

    @Column(name = "max_characters")
    @JsonProperty(value = "max_characters")
    Long maxCharacters;

    @NotNull
    @Column(name = "exclude_duplicates")
    @JsonProperty(value = "exclude_duplicates")
    Boolean excludeDuplicates;

    @NotNull
    @Column(name = "exclude_identical")
    @JsonProperty(value = "exclude_identical")
    Boolean excludeIdentical;

    @NotNull
    @Column(name = "exclude_test")
    @JsonProperty(value = "exclude_test")
    Boolean excludeTest;

    @NotNull
    @Column(name = "exclude_non_ascii")
    @JsonProperty(value = "exclude_non_ascii")
    Boolean excludeNonAscii;

    @JsonProperty("granularity")
    public abstract String getGranularity();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CodeQuery codeQuery = (CodeQuery) o;
        return id != null && Objects.equals(id, codeQuery.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                language,
                includeAst,
                minTokens,
                maxTokens,
                minLines,
                maxLines,
                minCharacters,
                maxCharacters,
                excludeDuplicates,
                excludeIdentical,
                excludeTest,
                excludeNonAscii
        );
    }
}
