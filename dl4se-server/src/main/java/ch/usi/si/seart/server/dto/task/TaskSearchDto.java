package ch.usi.si.seart.server.dto.task;

import ch.usi.si.seart.model.task.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TaskSearchDto {

    String uuid;
    Status status;

    public boolean hasUuid() {
        return uuid != null && !uuid.isBlank();
    }

    public boolean hasStatus() {
        return status != null;
    }
}
