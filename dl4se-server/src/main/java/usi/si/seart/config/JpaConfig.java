package usi.si.seart.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = {
        "usi.si.seart.model",
        "usi.si.seart.views",
})
public class JpaConfig {
}
