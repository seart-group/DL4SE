package usi.si.seart.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.type.StringEnumType;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeDefs(@TypeDef(name = "string-enum", typeClass = StringEnumType.class))
public class User {

    @Id
    @GeneratedValue
    @JsonIgnore
    Long id;

    @NotBlank
    @Column(unique = true)
    String email;

    @NotBlank
    @ToString.Exclude
    @JsonIgnore
    String password;

    @NotNull
    @Builder.Default
    Boolean verified = false;

    @NotNull
    @Builder.Default
    Boolean enabled = true;

    @NotBlank
    String organisation;

    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Type(type = "string-enum")
    Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Singular
    @ToString.Exclude
    @JsonIgnore
    List<Task> tasks = new ArrayList<>();

    @NotNull
    LocalDateTime registered;

    @PrePersist
    private void prePersist() {
        registered = LocalDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }
}
