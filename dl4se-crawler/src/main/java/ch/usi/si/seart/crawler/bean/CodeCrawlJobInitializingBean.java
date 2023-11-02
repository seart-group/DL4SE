package ch.usi.si.seart.crawler.bean;

import ch.usi.si.seart.bean.CrawlJobInitializingBean;
import ch.usi.si.seart.model.job.Job;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CodeCrawlJobInitializingBean extends CrawlJobInitializingBean {

    public CodeCrawlJobInitializingBean() {
        super(Job.CODE, LocalDateTime.of(2008, 1, 1, 0, 0, 0));
    }
}
