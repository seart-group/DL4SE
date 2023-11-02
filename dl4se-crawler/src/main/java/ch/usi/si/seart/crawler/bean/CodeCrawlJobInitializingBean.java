package ch.usi.si.seart.crawler.bean;

import ch.usi.si.seart.bean.CrawlJobInitializingBean;
import ch.usi.si.seart.crawler.config.properties.CrawlerProperties;
import ch.usi.si.seart.model.job.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CodeCrawlJobInitializingBean extends CrawlJobInitializingBean {

    @Autowired
    public CodeCrawlJobInitializingBean(CrawlerProperties properties) {
        super(Job.CODE, properties.getStartDate().atStartOfDay());
    }
}
