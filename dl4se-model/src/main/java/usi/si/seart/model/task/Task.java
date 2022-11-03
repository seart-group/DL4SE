package usi.si.seart.model.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import usi.si.seart.model.task.processing.Processing;
import usi.si.seart.model.task.query.Query;
import usi.si.seart.model.type.StringEnumType;
import usi.si.seart.model.user.User;
import usi.si.seart.validation.constraints.PositiveOrZeroOrNull;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "task")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dataset", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED)
@TypeDef(name = "string-enum", typeClass = StringEnumType.class)
public abstract class Task {

    @Id
    @GeneratedValue
    @JsonIgnore
    Long id;

    @NotNull
    @Type(type = "pg-uuid")
    @Column(unique = true)
    @Builder.Default
    UUID uuid = UUID.randomUUID();

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    User user;

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    Query query;

    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    Processing processing;

    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Type(type = "string-enum")
    @Builder.Default
    Status status = Status.QUEUED;

    @Version
    @JsonIgnore
    Long version;

    @Column(name = "checkpoint_id")
    @JsonProperty(value = "checkpoint_id")
    Long checkpointId;

    @PositiveOrZero
    @Column(name = "processed_results")
    @JsonProperty(value = "processed_results")
    @Builder.Default
    Long processedResults = 0L;

    @PositiveOrZero
    @Column(name = "total_results")
    @JsonProperty(value = "total_results")
    @Builder.Default
    Long totalResults = 0L;

    @PastOrPresent
    LocalDateTime submitted;

    LocalDateTime started;

    LocalDateTime finished;

    @NotNull
    @Builder.Default
    Boolean expired = false;

    @Column(name = "error_stack_trace")
    @JsonProperty(value = "error_stack_trace")
    String errorStackTrace;

    @PositiveOrZeroOrNull
    Long size;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Task task = (Task) o;
        return id != null && Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, query, processing);
    }
}
