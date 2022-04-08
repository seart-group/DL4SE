package usi.si.seart.model.task.query;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "file_query")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileQuery extends CodeQuery {

    @NotNull
    @Column(name = "exclude_unparsable")
    Boolean excludeUnparsable;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FileQuery fileQuery = (FileQuery) o;
        return id != null && Objects.equals(id, fileQuery.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getClass().hashCode(), excludeUnparsable);
    }
}
