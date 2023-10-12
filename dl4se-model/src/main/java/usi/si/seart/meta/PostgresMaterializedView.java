package usi.si.seart.meta;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Immutable
@Table(name = "pg_matviews")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostgresMaterializedView {

    @Id
    @Column(name = "matviewname")
    String name;

    @NotBlank
    @Column(name = "schemaname")
    String schema;

    @NotBlank
    @Column(name = "matviewowner")
    String owner;

    @NotNull
    @Column(name = "hasindexes")
    Boolean indexed;

    @NotNull
    @Column(name = "ispopulated")
    Boolean populated;
}
