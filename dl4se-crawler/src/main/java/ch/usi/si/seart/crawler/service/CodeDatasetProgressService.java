package ch.usi.si.seart.crawler.service;

import ch.usi.si.seart.crawler.config.properties.CrawlerProperties;
import ch.usi.si.seart.model.dataset.Dataset;
import ch.usi.si.seart.repository.DatasetProgressRepository;
import ch.usi.si.seart.service.AbstractDatasetProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeDatasetProgressService extends AbstractDatasetProgressService {

    @Autowired
    public CodeDatasetProgressService(
            CrawlerProperties crawlerProperties, DatasetProgressRepository datasetProgressRepository
    ) {
        super(Dataset.CODE, crawlerProperties.getStartDate().atStartOfDay(), datasetProgressRepository);
    }
}
