package ch.usi.si.seart.model.code;

import ch.usi.si.seart.model.GitRepo;
import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.validation.constraints.SHAHash;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class Code {

    @Id
    @GeneratedValue
    Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name="repo_id")
    GitRepo repo;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name="lang_id")
    @JsonUnwrapped(prefix = "language_")
    Language language;

    @NotNull
    String content;

    @NotNull
    @SHAHash
    @Column(name = "content_hash")
    @JsonProperty(value = "content_hash")
    String contentHash;

    @NotNull
    String ast;

    @NotNull
    @SHAHash
    @Column(name = "ast_hash")
    @JsonProperty(value = "ast_hash")
    String astHash;

    @NotNull
    @Column(name = "symbolic_expression")
    @JsonProperty(value = "symbolic_expression")
    String symbolicExpression;

    @NotNull
    @PositiveOrZero
    @Column(name = "total_tokens")
    @JsonProperty(value = "total_tokens")
    Long totalTokens;

    @NotNull
    @PositiveOrZero
    @Column(name = "code_tokens")
    @JsonProperty(value = "code_tokens")
    Long codeTokens;

    @NotNull
    @PositiveOrZero
    Long lines;

    @NotNull
    @PositiveOrZero
    Long characters;

    @NotNull
    @Column(name = "contains_non_ascii")
    @JsonProperty(value = "contains_non_ascii")
    Boolean containsNonAscii;

    @NotNull
    @Column(name = "contains_error")
    @JsonProperty(value = "contains_error")
    Boolean containsError;
}
