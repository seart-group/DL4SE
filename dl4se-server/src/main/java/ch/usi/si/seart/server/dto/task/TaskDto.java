package ch.usi.si.seart.server.dto.task;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskDto {

    @NotNull
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    JsonNode query = JsonNodeFactory.instance.objectNode();

    @NotNull
    @JsonSetter(nulls = Nulls.SKIP)
    @Builder.Default
    JsonNode processing = JsonNodeFactory.instance.objectNode();
}
