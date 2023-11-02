package ch.usi.si.seart.crawler.service;

import ch.usi.si.seart.exception.CrawlJobNotFoundException;
import ch.usi.si.seart.model.job.CrawlJob;
import ch.usi.si.seart.model.job.CrawlJob_;
import ch.usi.si.seart.model.job.Job;
import ch.usi.si.seart.repository.CrawlJobRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CrawlJobService {

    CrawlJob getProgress(Job jobType);
    void saveProgress(Job jobType, LocalDateTime checkpoint);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class CrawlJobServiceImpl implements CrawlJobService {

        CrawlJobRepository crawlJobRepository;

        @Override
        public CrawlJob getProgress(Job jobType) {
            Optional<CrawlJob> optional = crawlJobRepository.findByJobType(jobType);
            return optional.orElseThrow(() -> new CrawlJobNotFoundException(CrawlJob_.jobType, jobType));
        }

        @Override
        public void saveProgress(Job jobType, LocalDateTime checkpoint) {
            Optional<CrawlJob> optional = crawlJobRepository.findByJobType(jobType);
            CrawlJob crawlJob = optional.orElseThrow(IllegalStateException::new);
            crawlJob.setCheckpoint(checkpoint);
            crawlJobRepository.save(crawlJob);
        }
    }
}
