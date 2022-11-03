package usi.si.seart.scheduling;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ViewMaintainer implements Runnable {

    EntityManager entityManager;

    PlatformTransactionManager transactionManager;

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        String selectMaterialViews = "SELECT matviewname AS material_view_name FROM pg_matviews;";
        List<String> views = entityManager.createNativeQuery(selectMaterialViews).getResultList();
        String statements = views.stream()
                .map(view -> String.format("REFRESH MATERIALIZED VIEW CONCURRENTLY %s;", view))
                .collect(Collectors.joining());
        log.debug("Refreshing materialized views...");
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.executeWithoutResult(status -> entityManager.createNativeQuery(statements).executeUpdate());
        log.debug("Finished refreshing materialized views.");
    }
}
