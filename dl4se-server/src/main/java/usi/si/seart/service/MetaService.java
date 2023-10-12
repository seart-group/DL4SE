package usi.si.seart.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

public interface MetaService {

    void refreshMaterializedViews();

    @Service
    @ConditionalOnMissingBean(MetaService.class)
    class MetaServiceStub implements MetaService {

        @Override
        public void refreshMaterializedViews() {
        }
    }
}
