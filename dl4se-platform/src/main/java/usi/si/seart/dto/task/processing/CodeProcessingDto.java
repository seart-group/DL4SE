package usi.si.seart.dto.task.processing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import usi.si.seart.validation.constraints.NullOrNotBlank;
import usi.si.seart.validation.constraints.NullOrRange;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CodeProcessingDto {

    @NotNull
    @JsonProperty(value = "remove_docstring")
    Boolean removeDocstring;

    @NotNull
    @JsonProperty(value = "remove_inner_comments")
    Boolean removeInnerComments;

    @NullOrNotBlank
    @JsonProperty(value = "mask_token")
    String maskToken;

    @NullOrRange(min = 1, max = 100)
    @JsonProperty(value = "mask_percentage")
    Integer maskPercentage;

    @JsonProperty(value = "mask_contiguous_only")
    Boolean maskContiguousOnly;

    @NotNull
    List<@NotBlank String> idioms;
}
