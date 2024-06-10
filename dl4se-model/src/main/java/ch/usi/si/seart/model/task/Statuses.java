package ch.usi.si.seart.model.task;

import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class Statuses {

    public static final Set<Status> ACTIVE = Set.of(Status.QUEUED, Status.EXECUTING);
    public static final Set<Status> INACTIVE = Set.of(Status.FINISHED, Status.CANCELLED, Status.ERROR);
}
