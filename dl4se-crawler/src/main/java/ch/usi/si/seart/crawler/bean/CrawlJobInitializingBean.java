package ch.usi.si.seart.crawler.bean;

import ch.usi.si.seart.model.job.CrawlJob;
import ch.usi.si.seart.model.job.Job;
import ch.usi.si.seart.repository.CrawlJobRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CrawlJobInitializingBean implements InitializingBean {

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
