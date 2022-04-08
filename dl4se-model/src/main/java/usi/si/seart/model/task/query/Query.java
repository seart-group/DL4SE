package usi.si.seart.model.task.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import usi.si.seart.model.task.Task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
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
}
