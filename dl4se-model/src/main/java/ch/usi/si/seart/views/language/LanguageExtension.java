package ch.usi.si.seart.views.language;

import ch.usi.si.seart.model.Language;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Entity
@Immutable
@Table(name = "language_extension")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LanguageExtension {

    @EmbeddedId
    Key key;

    @ManyToOne(optional = false)
    @MapsId("languageId")
    @JoinColumn(name = "lang_id")
    Language language;

    @NotBlank
    String extension;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Embeddable
    public static class Key implements Serializable {

        @NotNull
        @Column(name = "lang_id")
        Long languageId;

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            Key key = (Key) object;
            return Objects.equals(languageId, key.languageId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(languageId);
        }
    }
}
