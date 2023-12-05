package ch.usi.si.seart.crawler.service;

import ch.usi.si.seart.crawler.config.properties.CrawlerProperties;
import ch.usi.si.seart.exception.CrawlJobNotFoundException;
import ch.usi.si.seart.model.job.CrawlJob;
import ch.usi.si.seart.model.job.CrawlJob_;
import ch.usi.si.seart.model.job.Job;
import ch.usi.si.seart.repository.CrawlJobRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CrawlJobService {

    CrawlJob getProgress(Job job);
    void saveProgress(Job job, LocalDateTime checkpoint);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    class CrawlJobServiceImpl implements CrawlJobService, InitializingBean {

        LocalDateTime startDateTime;

        CrawlJobRepository crawlJobRepository;

        @Autowired
        public CrawlJobServiceImpl(CrawlerProperties crawlerProperties, CrawlJobRepository crawlJobRepository) {
            this.startDateTime = crawlerProperties.getStartDate().atStartOfDay();
            this.crawlJobRepository = crawlJobRepository;
        }

        @Override
        public CrawlJob getProgress(Job job) {
            Optional<CrawlJob> optional = crawlJobRepository.findByJob(job);
            return optional.orElseThrow(() -> new CrawlJobNotFoundException(CrawlJob_.job, job));
        }

        @Override
        public void saveProgress(Job job, LocalDateTime checkpoint) {
            Optional<CrawlJob> optional = crawlJobRepository.findByJob(job);
            CrawlJob crawlJob = optional.orElseThrow(IllegalStateException::new);
            crawlJob.setCheckpoint(checkpoint);
            crawlJobRepository.save(crawlJob);
        }

        @Override
        public void afterPropertiesSet() {
            Optional<CrawlJob> optional = crawlJobRepository.findByJob(Job.CODE);
            CrawlJob crawlJob = optional.orElseGet(this::initialize);
            crawlJobRepository.save(crawlJob);
        }

        private CrawlJob initialize() {
            return CrawlJob.builder()
                    .checkpoint(startDateTime)
                    .job(Job.CODE)
                    .build();
        }
    }
}
