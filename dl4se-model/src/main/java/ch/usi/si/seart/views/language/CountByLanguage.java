package ch.usi.si.seart.views.language;

import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.views.GroupedCount;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Immutable
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class CountByLanguage implements GroupedCount<Language> {

    @Id
    @Column(name = "lang_id")
    Long id;

    @Getter
    @OneToOne(optional = false)
    @PrimaryKeyJoinColumn(name = "lang_id", referencedColumnName = "id")
    Language language;

    @NotNull
    @Getter(onMethod_ = @Override)
    Long count;

    @Override
    public Language getKey() {
        return language;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CountByLanguage other = (CountByLanguage) obj;
        return Objects.equals(language, other.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language);
    }
}
