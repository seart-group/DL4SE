package ch.usi.si.seart.server.config;

import ch.usi.si.seart.server.converter.RegisterDtoToUserConverter;
import ch.usi.si.seart.server.converter.StringToDatasetConverter;
import ch.usi.si.seart.server.converter.TaskSearchDtoToTaskSpecificationConverter;
import ch.usi.si.seart.server.converter.UserSearchDtoToSpecificationConverter;
import ch.usi.si.seart.server.converter.UserToTaskSpecificationConverter;
import ch.usi.si.seart.server.converter.UserToUserPrincipalConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableSpringDataWebSupport
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins("*")
                .exposedHeaders("Content-Type", "Content-Disposition", "Content-Length", "Date");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new RegisterDtoToUserConverter());
        registry.addConverter(new StringToDatasetConverter());
        registry.addConverter(new TaskSearchDtoToTaskSpecificationConverter());
        registry.addConverter(new UserSearchDtoToSpecificationConverter());
        registry.addConverter(new UserToTaskSpecificationConverter());
        registry.addConverter(new UserToUserPrincipalConverter());
    }
}
