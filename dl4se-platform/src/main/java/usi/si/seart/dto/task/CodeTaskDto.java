package usi.si.seart.dto.task;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import usi.si.seart.dto.task.processing.CodeProcessingDto;
import usi.si.seart.dto.task.query.CodeQueryDto;
import usi.si.seart.dto.task.query.FileQueryDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CodeTaskDto {

    @Valid
    @NotNull
    @JsonSetter(nulls = Nulls.SKIP)
    CodeQueryDto query = new FileQueryDto();

    @Valid
    @NotNull
    @JsonSetter(nulls = Nulls.SKIP)
    CodeProcessingDto processing = new CodeProcessingDto();
}
