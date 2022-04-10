package usi.si.seart.model.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import usi.si.seart.model.task.processing.Processing;
import usi.si.seart.model.task.query.Query;
import usi.si.seart.model.type.StringEnumType;
import usi.si.seart.model.user.User;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "task")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeDefs(@TypeDef(name = "string-enum", typeClass = StringEnumType.class))
public class Task {

    @Id
    @GeneratedValue
    @JsonIgnore
    Long id;

    @NotNull
    @Type(type = "pg-uuid")
    @Column(unique = true)
    UUID uuid;

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

    // TODO 06.04.22: Add some kind of reference that points to task executor instance

    @NotBlank
    @Column(name = "export_path")
    String exportPath;

    @Column(name = "checkpoint_id")
    Long checkpointId;

    @Column(name = "processed_results")
    Long processedResults;

    @Column(name = "total_results")
    Long totalResults;

    @PastOrPresent
    LocalDateTime submitted;

    LocalDateTime started;

    LocalDateTime finished;

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
