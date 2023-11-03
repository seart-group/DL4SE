package ch.usi.si.seart.scheduling.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
abstract class LoggingRunnable implements Runnable {

    Runnable delegate;

    protected void beforeRun() {
    }

    protected void afterRun() {
    }

    @Override
    public final void run() {
        beforeRun();
        delegate.run();
        afterRun();
    }

    @Override
    public final String toString() {
        return getClass().getSimpleName() + " for " + delegate;
    }
}
