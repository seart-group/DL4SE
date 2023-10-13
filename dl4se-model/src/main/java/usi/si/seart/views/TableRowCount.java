package usi.si.seart.views;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Immutable
@Table(name = "table_row_count")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TableRowCount {

    @Id
    String table;

    @NotNull
    Long count;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TableRowCount other = (TableRowCount) obj;
        return Objects.equals(table, other.table);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table);
    }
}
