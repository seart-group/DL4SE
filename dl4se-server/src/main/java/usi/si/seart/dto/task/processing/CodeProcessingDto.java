package usi.si.seart.dto.task.processing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import usi.si.seart.validation.constraints.AllNullOrNotNull;
import usi.si.seart.validation.constraints.NullOrNotBlank;
import usi.si.seart.validation.constraints.NullOrRange;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@AllNullOrNotNull(fields = { "maskToken", "maskPercentage", "maskContiguousOnly" })
public class CodeProcessingDto {

    @NotNull
    @JsonProperty(value = "remove_docstring")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    Boolean removeDocstring = false;

    @NotNull
    @JsonProperty(value = "remove_inner_comments")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    Boolean removeInnerComments = false;

    @NullOrNotBlank
    @JsonProperty(value = "mask_token")
    String maskToken;

    @NullOrRange(min = 1, max = 100)
    @JsonProperty(value = "mask_percentage")
    Integer maskPercentage;

    @JsonProperty(value = "mask_contiguous_only")
    Boolean maskContiguousOnly;

    @NotNull
    @JsonProperty(value = "abstract_code")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    Boolean abstractCode = false;

    @NotNull
    @JsonProperty(value = "abstract_idioms")
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    List<@NotBlank String> abstractIdioms = new ArrayList<>();
}
