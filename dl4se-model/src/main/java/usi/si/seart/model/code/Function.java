package usi.si.seart.model.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

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
    public int hashCode() {
        return Objects.hash(repo, file, content);
    }
}
