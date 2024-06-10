package ch.usi.si.seart.scheduling.support;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FixedRateLoggingRunnable extends LoggingRunnable {

    long period;

    public FixedRateLoggingRunnable(Runnable delegate, long period) {
        super(delegate);
        this.period = period;
    }

    @Override
    protected void beforeRun() {
        LocalDateTime nextRun = LocalDateTime.now()
                .plus(period, ChronoUnit.MILLIS)
                .truncatedTo(ChronoUnit.SECONDS);
        log.info("Started! Next run scheduled for: {}", nextRun);
    }
}
