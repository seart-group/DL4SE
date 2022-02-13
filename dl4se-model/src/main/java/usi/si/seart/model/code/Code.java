package usi.si.seart.model.code;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
import java.util.Objects;

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

    @ManyToOne(optional = false)
    @JoinColumn(name="repo_id", nullable=false)
    GitRepo repo;

    @ManyToOne(optional = false)
    @JoinColumn(name="lang_id", nullable=false)
    Language language;

    @Column(nullable = false)
    String content;

    @Column(name = "content_hash", nullable = false)
    String contentHash;

    @Column(nullable = false)
    String ast;

    @Column(name = "ast_hash", nullable = false)
    String astHash;

    @Column(name = "is_parsed", nullable = false, columnDefinition = "boolean default false")
    Boolean isParsed = false;

    @Column(nullable = false)
    Long tokens;

    @Column(nullable = false)
    Long lines;

    @Column(nullable = false)
    Long characters;

    @Column(name = "is_test", nullable = false)
    Boolean isTest;

    @Column(name = "contains_non_ascii", nullable = false)
    Boolean containsNonAscii;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Code code = (Code) o;
        return id != null && Objects.equals(id, code.id);
    }
}
