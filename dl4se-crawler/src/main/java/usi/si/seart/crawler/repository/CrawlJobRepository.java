package usi.si.seart.crawler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.model.job.CrawlJob;
import usi.si.seart.model.job.Job;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface CrawlJobRepository extends JpaRepository<CrawlJob, Long> {

    Optional<CrawlJob> findByJobType(@NotNull Job jobType);
}
