package ch.usi.si.seart.model.code;

import ch.usi.si.seart.model.meta.TreeSitterVersion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(
        name = "file",
        uniqueConstraints = @UniqueConstraint(columnNames = {"repo_id", "path"})
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class File extends Code {

    @NotBlank
    String path;

    @NotNull
    @Column(name = "is_test")
    @JsonProperty(value = "is_test")
    @Builder.Default
    Boolean isTest = false;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name="version_id")
    @JsonProperty(value = "tree_sitter_version")
    TreeSitterVersion treeSitterVersion;

    @OneToMany(mappedBy = "file", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    @JsonIgnore
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
        return Objects.hash(repo.getName(), path, contentHash, getClass().hashCode());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", File.class.getSimpleName() + "(", ")")
                .add("id=" + id)
                .add("repo=" + repo)
                .add("path='" + path + "'")
                .add("language=" + language)
                .toString();
    }
}
