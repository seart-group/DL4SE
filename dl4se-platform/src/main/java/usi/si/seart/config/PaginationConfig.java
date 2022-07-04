package usi.si.seart.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import usi.si.seart.service.ConfigurationService;

@Configuration
@EnableSpringDataWebSupport
@AllArgsConstructor(onConstructor_ = @Autowired)
public class PaginationConfig {

    ConfigurationService configurationService;

    @Bean
    PageableHandlerMethodArgumentResolverCustomizer pageableResolverCustomizer() {
        Integer maxPageSize = configurationService.get("max_page_size", Integer.class);
        return pageableResolver -> {
            pageableResolver.setMaxPageSize(maxPageSize);
            pageableResolver.setOneIndexedParameters(true);
        };
    }
}
