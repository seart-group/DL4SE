package ch.usi.si.seart.server.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public interface DatabaseService {

    void refreshMaterializedViews();

    @Service
    @ConditionalOnMissingBean(DatabaseService.class)
    class StubDatabaseService implements DatabaseService {

        @Override
        public void refreshMaterializedViews() {
        }
    }

    @Service
    @ConditionalOnProperty(value = "spring.jpa.database", havingValue = "postgresql")
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class PostgresDatabaseService implements DatabaseService {

        @PersistenceContext
        EntityManager entityManager;

        @Override
        @Transactional
        public void refreshMaterializedViews() {
            entityManager.createNativeQuery(
                "DO $$ " +
                "DECLARE " +
                    "_materialized_view_name text; " +
                    "_has_unique_index boolean; " +
                "BEGIN " +
                    "FOR _materialized_view_name, _has_unique_index IN (" +
                        "SELECT relname, indisunique " +
                        "FROM pg_class " +
                        "INNER JOIN pg_index " +
                            "ON pg_index.indrelid = pg_class.oid " +
                        "WHERE relkind = 'm' " +
                        "AND relnamespace IN (" +
                            "SELECT oid FROM pg_namespace " +
                            "WHERE nspname NOT LIKE 'pg_%' " +
                            "AND nspname != 'information_schema'" +
                        ")" +
                    ") " +
                    "LOOP EXECUTE 'REFRESH MATERIALIZED VIEW ' " +
                        "|| CASE WHEN _has_unique_index THEN 'CONCURRENTLY ' ELSE '' END " +
                        "|| _materialized_view_name; " +
                    "END LOOP; " +
                "END $$;"
            ).executeUpdate();
        }
    }
}
