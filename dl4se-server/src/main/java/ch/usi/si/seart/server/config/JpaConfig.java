package ch.usi.si.seart.server.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = {
        "ch.usi.si.seart.model",
        "ch.usi.si.seart.views",
})
public class JpaConfig {
}
