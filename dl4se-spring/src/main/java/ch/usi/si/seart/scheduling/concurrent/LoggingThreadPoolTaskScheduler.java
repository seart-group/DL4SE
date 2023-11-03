package ch.usi.si.seart.scheduling.concurrent;

import ch.usi.si.seart.scheduling.support.FixedDelayLoggingRunnable;
import ch.usi.si.seart.scheduling.support.FixedRateLoggingRunnable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

public class LoggingThreadPoolTaskScheduler extends ThreadPoolTaskScheduler {

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        Runnable decorated = new FixedRateLoggingRunnable(task, period);
        return super.scheduleAtFixedRate(decorated, startTime, period);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        Runnable decorated = new FixedRateLoggingRunnable(task, period);
        return super.scheduleAtFixedRate(decorated, period);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        Runnable decorated = new FixedDelayLoggingRunnable(task, delay);
        return super.scheduleWithFixedDelay(decorated, startTime, delay);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        Runnable decorated = new FixedDelayLoggingRunnable(task, delay);
        return super.scheduleWithFixedDelay(decorated, delay);
    }
}
