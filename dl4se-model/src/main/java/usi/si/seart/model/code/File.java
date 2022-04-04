package usi.si.seart.model.code;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
        name = "file",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"repo_id", "path"})}
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
    @Column(name = "is_parsed")
    @Builder.Default
    Boolean isParsed = false;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    @Singular
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

    public String toString() {
        return "File(id=" + this.id +
                ", repo=" + this.repo +
                ", language=" + this.language +
                ", path=" + this.path +
                ", content=" + this.content +
                ", contentHash=" + this.contentHash +
                ", ast=" + this.ast +
                ", astHash=" + this.astHash +
                ", isParsed=" + this.isParsed +
                ", totalTokens=" + this.totalTokens +
                ", codeTokens=" + this.codeTokens +
                ", lines=" + this.lines +
                ", characters=" + this.characters +
                ", isTest=" + this.isTest +
                ", containsNonAscii=" + this.containsNonAscii + ")";
    }
}
