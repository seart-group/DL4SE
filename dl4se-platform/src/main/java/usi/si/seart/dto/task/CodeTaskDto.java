package usi.si.seart.dto.task;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import usi.si.seart.dto.task.processing.CodeProcessingDto;
import usi.si.seart.dto.task.query.CodeQueryDto;

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
    CodeQueryDto query;

    @Valid
    @NotNull
    CodeProcessingDto processing;
}
