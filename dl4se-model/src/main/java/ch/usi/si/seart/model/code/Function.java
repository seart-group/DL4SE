package ch.usi.si.seart.model.code;

import ch.usi.si.seart.type.StringEnumType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "function")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeDefs(@TypeDef(name = "string-enum", typeClass = StringEnumType.class))
public class Function extends Code {

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name="file_id")
    @JsonUnwrapped(prefix = "file_")
    @JsonIgnoreProperties({
            "repo",
            "language",
            "content",
            "content_hash",
            "ast",
            "ast_hash",
            "symbolic_expression",
            "total_tokens",
            "code_tokens",
            "lines",
            "characters",
            "is_test",
            "contains_non_ascii",
            "contains_error",
    })
    File file;

    @Basic
    @Enumerated(EnumType.STRING)
    @Type(type = "string-enum")
    @Column(name = "boilerplate_type")
    @JsonProperty(value = "boilerplate_type")
    Boilerplate boilerplateType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Function function = (Function) o;
        return id != null && Objects.equals(id, function.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repo.getName(), file.getPath(), contentHash, getClass().hashCode());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Function.class.getSimpleName() + "(", ")")
                .add("id=" + id)
                .add("repo=" + repo)
                .add("file=" + file)
                .add("language=" + language)
                .toString();
    }
}
