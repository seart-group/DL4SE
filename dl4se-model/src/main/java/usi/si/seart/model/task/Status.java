package usi.si.seart.model.task;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

public enum Status {
    QUEUED, EXECUTING, FINISHED, CANCELLED, ERROR;

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Category {
        public static final Set<Status> ACTIVE = Set.of(QUEUED, EXECUTING);
        public static final Set<Status> INACTIVE = Set.of(FINISHED, CANCELLED, ERROR);
    }
}
