package usi.si.seart.dto.task.processing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CodeProcessingDto {

    @NotBlank
    @JsonProperty(value = "mask_token")
    String maskToken;

    @Range(min = 0, max = 100)
    @JsonProperty(value = "mask_percentage")
    Integer maskPercentage;

    @NotNull
    @JsonProperty(value = "mask_contiguous_only")
    Boolean maskContiguousOnly;

    @NotNull
    List<@NotNull String> idioms;
}
