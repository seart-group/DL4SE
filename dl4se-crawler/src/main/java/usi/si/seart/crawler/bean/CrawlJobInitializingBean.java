package usi.si.seart.crawler.bean;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import usi.si.seart.crawler.repository.CrawlJobRepository;
import usi.si.seart.model.job.CrawlJob;
import usi.si.seart.model.job.Job;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CrawlJobInitializingBean implements InitializingBean {

    @NonFinal
    @Value("${app.crawl-job.type}")
    Job jobType;

    LocalDateTime defaultStartDateTime;

    CrawlJobRepository crawlJobRepository;

    @Override
    public void afterPropertiesSet() {
        Optional<CrawlJob> optional = crawlJobRepository.findByJobType(jobType);
        CrawlJob crawlJob = optional.orElseGet(
                () -> CrawlJob.builder()
                        .checkpoint(defaultStartDateTime)
                        .jobType(jobType)
                        .build()
        );
        crawlJobRepository.save(crawlJob);
    }
}