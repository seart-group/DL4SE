package usi.si.seart.model.task.processing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import usi.si.seart.validation.constraints.NullOrNotBlank;
import usi.si.seart.validation.constraints.NullOrRange;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
@DiscriminatorValue("CODE")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class CodeProcessing extends Processing {

    @NotNull
    @Column(name = "remove_docstring")
    @JsonProperty(value = "remove_docstring")
    Boolean removeDocstring;

    @NotNull
    @Column(name = "remove_inner_comments")
    @JsonProperty(value = "remove_inner_comments")
    Boolean removeInnerComments;

    @NullOrNotBlank
    @Column(name = "mask_token")
    @JsonProperty(value = "mask_token")
    String maskToken;

    @NullOrRange(min = 1, max = 100)
    @Column(name = "mask_percentage")
    @JsonProperty(value = "mask_percentage")
    Integer maskPercentage;

    @Column(name = "mask_contiguous_only")
    @JsonProperty(value = "mask_contiguous_only")
    Boolean maskContiguousOnly;

    @NotNull
    @Type(type = "list-array")
    @Singular
    @ToString.Exclude
    List<@NotBlank String> idioms = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CodeProcessing that = (CodeProcessing) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        SortedSet<String> sortedIdioms = new TreeSet<>(idioms);
        return Objects.hash(
                super.hashCode(),
                removeDocstring,
                removeInnerComments,
                maskToken,
                maskPercentage,
                maskContiguousOnly,
                sortedIdioms
        );
    }
}
