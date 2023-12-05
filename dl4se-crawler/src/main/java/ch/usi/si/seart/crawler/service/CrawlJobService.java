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

    CrawlJob getProgress(Job jobType);
    void saveProgress(Job jobType, LocalDateTime checkpoint);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    class CrawlJobServiceImpl implements CrawlJobService, InitializingBean {

        private static final Job JOB_TYPE = Job.CODE;

        LocalDateTime startDateTime;

        CrawlJobRepository crawlJobRepository;

        @Autowired
        public CrawlJobServiceImpl(CrawlerProperties crawlerProperties, CrawlJobRepository crawlJobRepository) {
            this.startDateTime = crawlerProperties.getStartDate().atStartOfDay();
            this.crawlJobRepository = crawlJobRepository;
        }

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

        @Override
        public void afterPropertiesSet() {
            Optional<CrawlJob> optional = crawlJobRepository.findByJobType(JOB_TYPE);
            CrawlJob crawlJob = optional.orElseGet(this::initialize);
            crawlJobRepository.save(crawlJob);
        }

        private CrawlJob initialize() {
            return CrawlJob.builder()
                    .checkpoint(startDateTime)
                    .jobType(JOB_TYPE)
                    .build();
        }
    }
}
