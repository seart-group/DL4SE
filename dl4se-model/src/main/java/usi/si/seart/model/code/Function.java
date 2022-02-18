package usi.si.seart.model.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "function")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Function extends Code {

    @ManyToOne(optional = false)
    @JoinColumn(name="file_id", nullable=false)
    File file;

    @Column(name = "is_boilerplate", nullable = false)
    Boolean isBoilerplate;

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

    public String toString() {
        return "Function(id=" + this.id +
                ", repo=" + this.repo +
                ", file=" + this.file +
                ", language=" + this.language +
                ", content=" + this.content +
                ", contentHash=" + this.contentHash +
                ", ast=" + this.ast +
                ", astHash=" + this.astHash +
                ", tokens=" + this.tokens +
                ", lines=" + this.lines +
                ", characters=" + this.characters +
                ", isTest=" + this.isTest +
                ", isBoilerplate=" + this.isBoilerplate +
                ", containsNonAscii=" + this.containsNonAscii + ")";
    }
}
