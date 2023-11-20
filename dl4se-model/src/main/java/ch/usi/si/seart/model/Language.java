package ch.usi.si.seart.model;

import ch.usi.si.seart.validation.constraints.FileExtension;
import ch.usi.si.seart.validation.constraints.LanguageName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "language")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeDefs(@TypeDef(name = "list-array", typeClass = ListArrayType.class))
public class Language {

    @Id
    @GeneratedValue
    @JsonIgnore
    Long id;

    @LanguageName
    @Column(unique = true)
    String name;

    @NotEmpty
    @Type(type = "list-array")
    @Column(columnDefinition = "text[]")
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    List<@FileExtension String> extensions = new ArrayList<>();

    @ManyToMany(mappedBy = "languages")
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    Set<GitRepo> repos = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return id != null && Objects.equals(id, language.id);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
