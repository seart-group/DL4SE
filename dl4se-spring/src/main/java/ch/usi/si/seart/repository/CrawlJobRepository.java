package ch.usi.si.seart.repository;

import ch.usi.si.seart.model.job.CrawlJob;
import ch.usi.si.seart.model.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface CrawlJobRepository extends JpaRepository<CrawlJob, Long> {

    Optional<CrawlJob> findByJobType(@NotNull Job jobType);
}
