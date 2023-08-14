package usi.si.seart.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "git_repo")
@DynamicUpdate
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
    @JsonIgnore
    Long id;

    @NotBlank
    @Size(max = 140)
    @Column(unique = true)
    String name;

    String license;

    @NotNull
    @Column(name = "is_fork")
    @JsonProperty(value = "is_fork")
    Boolean isFork;

    @PositiveOrZero
    @Builder.Default
    Long commits = 0L;

    @PositiveOrZero
    @Builder.Default
    Long contributors = 0L;

    @PositiveOrZero
    @Builder.Default
    Long issues = 0L;

    @PositiveOrZero
    @Builder.Default
    Long stars = 0L;

    @NotNull
    @Column(name = "last_commit")
    @JsonProperty(value = "last_commit")
    @PastOrPresent
    @Builder.Default
    LocalDateTime lastCommit = LocalDateTime.of(2008, 1, 1, 0, 0);

    @NotBlank
    @Size(min = 7, max = 40)
    @Column(name = "last_commit_sha")
    @JsonProperty(value = "last_commit_sha")
    @Builder.Default
    String lastCommitSHA = "0000000000000000000000000000000000000000";

    @NotNull
    @Column(name = "is_unavailable")
    @JsonProperty(value = "is_unavailable")
    @Builder.Default
    Boolean isUnavailable = false;

    @OneToMany(mappedBy = "repo")
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "repo")
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    List<Function> functions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "git_repo_language",
            joinColumns = @JoinColumn(name = "repo_id"),
            inverseJoinColumns = @JoinColumn(name = "lang_id")
    )
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
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
