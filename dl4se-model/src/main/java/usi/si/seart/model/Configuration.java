package usi.si.seart.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "configuration")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Configuration {

    @Id
    @NotBlank
    String key;

    @NotNull
    String value;

    @NotNull
    @Column(name = "last_update")
    LocalDateTime lastUpdate;

    @PreUpdate
    private void preUpdate() {
        lastUpdate = LocalDateTime.now(ZoneOffset.UTC);
    }
}
