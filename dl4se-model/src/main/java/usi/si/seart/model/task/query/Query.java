package usi.si.seart.model.task.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import usi.si.seart.model.task.Task;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Objects;

@Entity
@Table(name = "query")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class Query {

    @Id
    @GeneratedValue
    @JsonIgnore
    Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "task_id")
    Task task;

    @NotNull
    @Column(name = "has_license")
    Boolean hasLicense;

    @NotNull
    @Column(name = "exclude_forks")
    Boolean excludeForks;

    @PositiveOrZero
    @Column(name = "min_commits")
    Long minCommits;

    @PositiveOrZero
    @Column(name = "min_contributors")
    Long minContributors;

    @PositiveOrZero
    @Column(name = "min_issues")
    Long minIssues;

    @PositiveOrZero
    @Column(name = "min_stars")
    Long minStars;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Query query = (Query) o;
        return id != null && Objects.equals(id, query.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getClass().hashCode(),
                hasLicense,
                excludeForks,
                minCommits,
                minContributors,
                minIssues,
                minStars
        );
    }
}
