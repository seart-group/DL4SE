package ch.usi.si.seart.server.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "ch.usi.si.seart.server.feign")
public class FeignConfig {
}
