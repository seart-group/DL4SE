package ch.usi.si.seart.bean;

import ch.usi.si.seart.model.job.CrawlJob;
import ch.usi.si.seart.model.job.Job;
import ch.usi.si.seart.repository.CrawlJobRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CrawlJobInitializingBean implements InitializingBean {

    Job jobType;

    LocalDateTime startDateTime;

    @NonFinal
    CrawlJobRepository crawlJobRepository;

    @Autowired
    public void setCrawlJobRepository(CrawlJobRepository crawlJobRepository) {
        this.crawlJobRepository = crawlJobRepository;
    }

    @Override
    public void afterPropertiesSet() {
        Optional<CrawlJob> optional = crawlJobRepository.findByJobType(jobType);
        CrawlJob crawlJob = optional.orElseGet(
                () -> CrawlJob.builder()
                        .checkpoint(startDateTime)
                        .jobType(jobType)
                        .build()
        );
        crawlJobRepository.save(crawlJob);
    }
}
