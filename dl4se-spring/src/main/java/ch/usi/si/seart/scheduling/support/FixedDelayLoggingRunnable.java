package ch.usi.si.seart.scheduling.support;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FixedDelayLoggingRunnable extends LoggingRunnable {

    long delay;

    public FixedDelayLoggingRunnable(Runnable delegate, long delay) {
        super(delegate);
        this.delay = delay;
    }

    @Override
    protected void afterRun() {
        LocalDateTime nextRun = LocalDateTime.now()
                .plus(delay, ChronoUnit.MILLIS)
                .truncatedTo(ChronoUnit.SECONDS);
        log.info("Finished! Next run scheduled for: {}", nextRun);
    }
}
