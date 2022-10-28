package usi.si.seart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import usi.si.seart.model.task.Status;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.user.User;

import javax.persistence.Tuple;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Long countAllByUser(User user);

    List<Task> findAllByStatus(Status status);

    Page<Task> findAllByUser(User user, Pageable pageable);

    Set<Task> findAllByUserAndStatusIn(User user, Collection<Status> status);

    Long countAllByUserAndStatusIn(User user, Collection<Status> status);

    Optional<Task> findFirstByStatusOrderBySubmitted(Status status);

    @Modifying
    @Query(
            value = "UPDATE Task SET " +
                    "status = usi.si.seart.model.task.Status.CANCELLED, " +
                    "finished = current_timestamp, " +
                    "version = version + 1, " +
                    "expired = true " +
                    "WHERE id = :id"
    )
    void markForCancellation(@Param("id") Long id);

    Stream<Task> findAllByFinishedLessThanAndExpired(LocalDateTime finished, @NotNull Boolean expired);

    Optional<Task> findByUuid(@NotNull UUID uuid);

    @Query("SELECT status, COUNT(status) AS COUNT FROM Task GROUP BY status")
    List<Tuple> countAllGroupByStatus();

    @Query("SELECT status, COUNT(status) AS COUNT FROM Task WHERE user = :user GROUP BY status")
    List<Tuple> countAllByUserGroupByStatus(@Param("user") User user);
}
