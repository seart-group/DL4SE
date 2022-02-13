package usi.si.seart.model.code;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import usi.si.seart.model.GitRepo;
import usi.si.seart.model.Language;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "file")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class File {

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
    String path;

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

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    List<Function> functions = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return id != null && Objects.equals(id, file.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, path);
    }
}
