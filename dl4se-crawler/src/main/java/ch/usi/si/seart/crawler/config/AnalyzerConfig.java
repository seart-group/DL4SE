package ch.usi.si.seart.crawler.config;

import ch.usi.si.seart.analyzer.Analyzer;
import ch.usi.si.seart.analyzer.AnalyzerCustomizer;
import ch.usi.si.seart.crawler.config.properties.AnalyzerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AnalyzerConfig {

    @Bean
    public AnalyzerCustomizer<Analyzer> analyzerCustomizer(AnalyzerProperties analyzerProperties) {
        return analyzer -> {
            Duration maxParseTime = analyzerProperties.getMaxParseTime();
            if (maxParseTime == null || maxParseTime.isNegative() || maxParseTime.isZero()) return;
            analyzer.setParserTimeout(maxParseTime);
        };
    }
}
