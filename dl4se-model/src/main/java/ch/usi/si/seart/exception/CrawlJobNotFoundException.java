package ch.usi.si.seart.exception;

import ch.usi.si.seart.model.job.CrawlJob;

import javax.persistence.metamodel.Attribute;

public class CrawlJobNotFoundException extends EntityNotFoundException {

    public <T> CrawlJobNotFoundException(Attribute<CrawlJob, T> attribute, T value) {
        super(CrawlJob.class, attribute, value);
    }
}
