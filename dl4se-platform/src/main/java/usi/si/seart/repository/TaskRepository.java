package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.user.User;

import java.util.Set;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.user = :user " +
            "AND t.status IN (usi.si.seart.model.task.Status.QUEUED, usi.si.seart.model.task.Status.EXECUTING)")
    Set<Task> findActiveByUser(@Param("user") User user);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user " +
            "AND t.status IN (usi.si.seart.model.task.Status.QUEUED, usi.si.seart.model.task.Status.EXECUTING)")
    Long countActiveByUser(@Param("user") User user);
}
