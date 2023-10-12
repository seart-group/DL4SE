package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usi.si.seart.meta.PostgresMaterializedView;
import usi.si.seart.repository.PostgresMaterializedViewRepository;

public interface MetaService {

    void refreshMaterializedViews();

    @Service
    @ConditionalOnMissingBean(MetaService.class)
    class MetaServiceStub implements MetaService {

        @Override
        public void refreshMaterializedViews() {
        }
    }

    @Service
    @ConditionalOnProperty(value = "spring.jpa.database", havingValue = "postgresql")
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class PostgresMetaService implements MetaService {

        PostgresMaterializedViewRepository materializedViewRepository;

        @Override
        @Transactional
        public void refreshMaterializedViews() {
            materializedViewRepository.findAll().stream()
                    .map(PostgresMaterializedView::getName)
                    .forEach(materializedViewRepository::refreshByName);
        }
    }
}
