package usi.si.seart.model.code;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import usi.si.seart.model.GitRepo;
import usi.si.seart.model.Language;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

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
    Language language;

    @NotNull
    String content;

    @NotNull
    @Column(name = "content_hash")
    String contentHash;

    String ast;

    @Column(name = "ast_hash")
    String astHash;

    @Column(name = "total_tokens")
    Long totalTokens;

    @Column(name = "code_tokens")
    Long codeTokens;

    @NotNull
    Long lines;

    @NotNull
    Long characters;

    @NotNull
    @Column(name = "is_test")
    @Builder.Default
    Boolean isTest = false;

    @NotNull
    @Column(name = "contains_non_ascii")
    Boolean containsNonAscii;
}
