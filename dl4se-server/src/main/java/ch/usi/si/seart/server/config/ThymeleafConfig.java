package ch.usi.si.seart.server.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;

@Configuration
public class ThymeleafConfig {

    @Bean
    public ThymeleafEvaluationContext thymeleafEvaluationContext(
            ApplicationContext applicationContext, ConversionService conversionService
    ) {
        return new ThymeleafEvaluationContext(applicationContext, conversionService);
    }
}
