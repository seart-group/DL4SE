package usi.si.seart.model.task.query;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import usi.si.seart.model.Language;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

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
    Language language;

    @NotNull
    @Column(name = "include_ast")
    Boolean includeAst;

    @Column(name = "min_tokens")
    Long minTokens;

    @Column(name = "max_tokens")
    Long maxTokens;

    @Column(name = "min_lines")
    Long minLines;

    @Column(name = "max_lines")
    Long maxLines;

    @Column(name = "min_characters")
    Long minCharacters;

    @Column(name = "max_characters")
    Long maxCharacters;

    @NotNull
    @Column(name = "exclude_duplicates")
    Boolean excludeDuplicates;

    @NotNull
    @Column(name = "exclude_identical")
    Boolean excludeIdentical;

    @NotNull
    @Column(name = "exclude_non_ascii")
    Boolean excludeNonAscii;
}
