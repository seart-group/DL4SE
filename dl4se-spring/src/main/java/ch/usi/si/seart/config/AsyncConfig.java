package ch.usi.si.seart.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.reflect.Method;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {

            private final Logger log = LoggerFactory.getLogger(
                    AsyncConfig.class.getCanonicalName() + "$" + AsyncUncaughtExceptionHandler.class.getSimpleName()
            );

            @Override
            public void handleUncaughtException(
                    @NonNull Throwable throwable, @NonNull Method method, @NonNull Object... params
            ) {
                log.error("Unhandled exception occurred while executing asynchronous method: {}", method, throwable);
            }
        };
    }
}
