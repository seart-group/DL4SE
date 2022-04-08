package usi.si.seart.model.task.processing;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "code_processing")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class CodeProcessing extends Processing {

    @NotNull
    @Column(name = "remove_docstring")
    Boolean removeDocstring;

    @NotNull
    @Column(name = "remove_inner_comments")
    Boolean removeInnerComments;

    @NotBlank
    @Column(name = "mask_token")
    String maskToken;

    @Range(min = 0, max = 100)
    @Column(name = "mask_percentage")
    Integer maskPercentage;

    @NotNull
    @Column(name = "mask_contiguous_only")
    Boolean maskContiguousOnly;

    @NotNull
    @Type(type = "list-array")
    @Singular
    @ToString.Exclude
    List<@NotNull String> idioms = new ArrayList<>();
}
