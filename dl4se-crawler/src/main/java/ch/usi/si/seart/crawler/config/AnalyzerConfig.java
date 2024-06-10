package ch.usi.si.seart.crawler.config;

import ch.usi.si.seart.analyzer.Analyzer;
import ch.usi.si.seart.analyzer.AnalyzerCustomizer;
import ch.usi.si.seart.crawler.config.properties.AnalyzerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AnalyzerConfig {

    @Bean
    public Executor analyzerExecutor(AnalyzerProperties analyzerProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(analyzerProperties.getCorePoolSize());
        executor.setMaxPoolSize(analyzerProperties.getMaxPoolSize());
        executor.setQueueCapacity(analyzerProperties.getQueueCapacity());
        executor.setThreadNamePrefix("analyzer-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean
    public AnalyzerCustomizer<Analyzer> analyzerCustomizer(AnalyzerProperties analyzerProperties) {
        return analyzer -> {
            Duration maxParseTime = analyzerProperties.getMaxParseTime();
            if (maxParseTime == null || maxParseTime.isNegative() || maxParseTime.isZero()) return;
            analyzer.setParserTimeout(maxParseTime);
        };
    }
}
