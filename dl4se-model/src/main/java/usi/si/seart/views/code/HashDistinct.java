package usi.si.seart.views.code;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Immutable;
import usi.si.seart.validation.constraints.Hash;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Objects;

@Entity
@Getter
@Immutable
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class HashDistinct {

    @Id
    Long id;

    @Hash
    String hash;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashDistinct that = (HashDistinct) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hash);
    }
}
