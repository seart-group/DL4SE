package ch.usi.si.seart.crawler.service;

import ch.usi.si.seart.crawler.repository.CrawlJobRepository;
import ch.usi.si.seart.model.job.CrawlJob;
import ch.usi.si.seart.model.job.Job;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CrawlJobService {

    CrawlJob getProgress();
    void saveProgress(LocalDateTime checkpoint);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class CrawlJobServiceImpl implements CrawlJobService {

        Job jobType;

        CrawlJobRepository crawlJobRepository;

        @Override
        public CrawlJob getProgress() {
            Optional<CrawlJob> optional = crawlJobRepository.findByJobType(jobType);
            return optional.orElseThrow(IllegalStateException::new);
        }

        @Override
        public void saveProgress(LocalDateTime checkpoint) {
            Optional<CrawlJob> optional = crawlJobRepository.findByJobType(jobType);
            CrawlJob crawlJob = optional.orElseThrow(IllegalStateException::new);
            crawlJob.setCheckpoint(checkpoint);
            crawlJobRepository.save(crawlJob);
        }
    }
}
