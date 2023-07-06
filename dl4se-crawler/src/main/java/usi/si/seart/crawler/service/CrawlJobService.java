package usi.si.seart.crawler.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usi.si.seart.crawler.repository.CrawlJobRepository;
import usi.si.seart.model.job.CrawlJob;
import usi.si.seart.model.job.Job;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CrawlJobService {

    CrawlJob getProgress();
    void saveProgress(LocalDateTime checkpoint);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(onConstructor_ = @Autowired)
    class CrawlJobServiceImpl implements CrawlJobService {

        @NonFinal
        @Value("${app.crawl-job.type}")
        Job jobType;

        CrawlJobRepository crawlJobRepository;

        @Override
        @Transactional
        public CrawlJob getProgress() {
            Optional<CrawlJob> optional = crawlJobRepository.findByJobType(jobType);
            return optional.orElseThrow(IllegalStateException::new);
        }

        @Override
        @Transactional
        public void saveProgress(LocalDateTime checkpoint) {
            Optional<CrawlJob> optional = crawlJobRepository.findByJobType(jobType);
            CrawlJob crawlJob = optional.orElseThrow(IllegalStateException::new);
            crawlJob.setCheckpoint(checkpoint);
            crawlJobRepository.save(crawlJob);
        }
    }
}
