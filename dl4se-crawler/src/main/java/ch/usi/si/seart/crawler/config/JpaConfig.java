package ch.usi.si.seart.crawler.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = "ch.usi.si.seart.model")
public class JpaConfig {
}
