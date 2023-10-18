package ch.usi.si.seart.server.scheduling;

import ch.usi.si.seart.server.service.DatabaseService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ViewMaintainer implements Runnable {

    DatabaseService databaseService;

    @Override
    public void run() {
        log.info("Refreshing materialized views...");
        databaseService.refreshMaterializedViews();
        log.info("Finished refreshing views.");
    }
}
