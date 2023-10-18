package ch.usi.si.seart.model.code;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
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
