package ch.usi.si.seart.model.user.token;

import ch.usi.si.seart.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "token")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class Token {

    @Id
    @GeneratedValue
    @JsonIgnore
    Long id;

    @NotBlank
    @Column(unique = true)
    String value;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    User user;

    @NotNull
    LocalDateTime expires;

    protected abstract LocalDateTime calculateExpiryDate();

    @PreUpdate
    @PrePersist
    private void beforeSaving() {
        expires = calculateExpiryDate();
    }

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return expires.isBefore(now);
    }
}
