package ch.usi.si.seart.server.dto.task;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.function.Predicate;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TaskSearchDto {

    String uuid;

    private static Predicate<String> notNull = Objects::nonNull;
    private static Predicate<String> notBlank = Predicate.not(String::isBlank);
    private static Predicate<String> specified = notNull.and(notBlank);

    public boolean hasUuid() {
        return specified.test(uuid);
    }
}
