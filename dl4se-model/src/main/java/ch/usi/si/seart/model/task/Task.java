package ch.usi.si.seart.model.task;

import ch.usi.si.seart.model.job.Job;
import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.type.StringEnumType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED)
@TypeDefs({
        @TypeDef(name = "string-enum", typeClass = StringEnumType.class),
        @TypeDef(name = "json", typeClass = JsonType.class)
})
public class Task {

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

    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Type(type = "string-enum")
    Job dataset;

    @NotNull
    @Type(type = "json")
    @Builder.Default
    JsonNode query = JsonNodeFactory.instance.objectNode();

    @NotNull
    @Type(type = "json")
    @Builder.Default
    JsonNode processing = JsonNodeFactory.instance.objectNode();

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

    @PositiveOrZero
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
        return Objects.hash(user, dataset, query, processing);
    }
}
