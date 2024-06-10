package ch.usi.si.seart.model.dataset;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
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

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Entity
@Table(name = "dataset_progress")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeDef(name = "postgres-enum", typeClass = PostgreSQLEnumType.class)
public class DatasetProgress {

    @Id
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Type(type = "postgres-enum")
    Dataset dataset;

    @NotNull
    @PastOrPresent
    LocalDateTime checkpoint;
}
