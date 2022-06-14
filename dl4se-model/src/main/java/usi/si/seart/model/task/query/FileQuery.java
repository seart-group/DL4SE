package usi.si.seart.model.task.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Objects;

@Entity
@DiscriminatorValue("CODE_FILE")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileQuery extends CodeQuery {

    @Column(name = "exclude_unparsable")
    @JsonProperty(value = "exclude_unparsable")
    Boolean excludeUnparsable;

    @Override
    public String getGranularity() {
        return "file";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FileQuery fileQuery = (FileQuery) o;
        return id != null && Objects.equals(id, fileQuery.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), excludeUnparsable);
    }
}
