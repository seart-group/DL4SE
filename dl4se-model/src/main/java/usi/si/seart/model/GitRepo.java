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
import javax.validation.constraints.NotNull;
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

    @NotNull
    @Column(unique = true, length = 140)
    String name;

    String license;

    @NotNull
    @Column(name = "is_fork")
    Boolean isFork;

    @NotNull
    @Builder.Default
    Long commits = 0L;

    @NotNull
    @Builder.Default
    Long contributors = 0L;

    @NotNull
    @Builder.Default
    Long issues = 0L;

    @NotNull
    @Builder.Default
    Long stars = 0L;

    @NotNull
    @Column(name = "last_commit", columnDefinition = "TIMESTAMP")
    @Builder.Default
    LocalDateTime lastCommit = LocalDateTime.of(2008, 1, 1, 0, 0);

    @NotNull
    @Column(name = "last_commit_sha", length = 40)
    @Builder.Default
    String lastCommitSHA = "0000000000000000000000000000000000000000";

    @NotNull
    @Column(name = "is_deleted")
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
