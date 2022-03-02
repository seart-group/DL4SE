package usi.si.seart.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "git_repo")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GitRepo {

    @Id
    @GeneratedValue
    Long id;

    @Column(unique = true, nullable = false, length = 140)
    String name;

    String license;

    @Column(name = "is_fork", nullable = false)
    Boolean isFork;

    @Column(nullable = false)
    @Builder.Default
    Long commits = 0L;

    @Column(nullable = false)
    @Builder.Default
    Long contributors = 0L;

    @Column(nullable = false)
    @Builder.Default
    Long issues = 0L;

    @Column(nullable = false)
    @Builder.Default
    Long stars = 0L;

    @Column(name = "last_update", nullable = false, columnDefinition = "TIMESTAMP")
    LocalDateTime lastUpdate;

    @Column(name = "last_commit_sha", nullable = false, length = 40)
    String lastCommitSHA;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    Boolean isDeleted = false;

    @OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Singular
    List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "repo", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Singular
    List<Function> functions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "git_repo_language",
            joinColumns = @JoinColumn(name = "repo_id"),
            inverseJoinColumns = @JoinColumn(name = "lang_id")
    )
    @ToString.Exclude
    @Singular
    Set<Language> languages = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitRepo gitRepo = (GitRepo) o;
        return id != null && Objects.equals(id, gitRepo.id);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
