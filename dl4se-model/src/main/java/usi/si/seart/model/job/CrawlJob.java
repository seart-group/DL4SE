package usi.si.seart.model.job;

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
import org.hibernate.annotations.TypeDefs;
import usi.si.seart.model.type.StringEnumType;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "crawl_job")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeDefs(@TypeDef(name = "string-enum", typeClass = StringEnumType.class))
public class CrawlJob {

    @Id
    @GeneratedValue
    Long id;

    @NotNull
    LocalDateTime checkpoint;

    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Type(type = "string-enum")
    @Column(name = "job_type")
    Job jobType;
}
