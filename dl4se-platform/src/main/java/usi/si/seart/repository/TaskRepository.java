package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import usi.si.seart.model.task.Status;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.user.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Set<Task> findAllByUserAndStatusIn(User user, Collection<Status> status);

    Long countAllByUserAndStatusIn(User user, Collection<Status> status);

    @Query(
            value = "SELECT t.* FROM task as t WHERE t.status = 'EXECUTING' ORDER BY submitted LIMIT 1",
            nativeQuery = true
    )
    Optional<Task> findFirstExecuting();

    @Modifying
    @Query(
            value = "UPDATE task SET status = 'EXECUTING', started = now() " +
                    "WHERE id = (SELECT id FROM task WHERE status = 'QUEUED' ORDER BY submitted LIMIT 1)",
            nativeQuery = true
    )
    void markForExecution();

    @Modifying
    @Query(
            "UPDATE Task SET status = usi.si.seart.model.task.Status.CANCELLED, version = version + 1 WHERE id = :id"
    )
    void markForCancellation(@Param("id") Long id);

    Optional<Task> findByUuid(@NotNull UUID uuid);

    List<Task> findByFinishedBetween(LocalDateTime lower, LocalDateTime upper);
}
