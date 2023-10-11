package usi.si.seart.scheduling;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ViewMaintainer implements Runnable {

    private static final String SELECT = "SELECT matviewname AS material_view_name FROM pg_matviews;";

    private static final String TEMPLATE = "REFRESH MATERIALIZED VIEW CONCURRENTLY %s;";

    @PersistenceContext
    EntityManager entityManager;

    PlatformTransactionManager transactionManager;

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        log.info("Recomputing statistics...");
        List<String> views = entityManager.createNativeQuery(SELECT).getResultList();
        String statements = views.stream()
                .map(view -> String.format(TEMPLATE, view))
                .collect(Collectors.joining());
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallback(statements));
        log.info("Finished updating statistics.");
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private class TransactionCallback extends TransactionCallbackWithoutResult {

        String statements;

        @Override
        protected void doInTransactionWithoutResult(TransactionStatus status) {
            entityManager.createNativeQuery(statements).executeUpdate();
        }
    }
}
