package usi.si.seart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import usi.si.seart.converter.DtoToConfigurationConverter;
import usi.si.seart.converter.DtoToUserConverter;
import usi.si.seart.converter.TaskSearchDtoToSpecificationConverter;
import usi.si.seart.converter.UserSearchDtoToSpecificationConverter;
import usi.si.seart.converter.UserToUserPrincipalConverter;

@Configuration
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
        registry.addConverter(new DtoToUserConverter());
        registry.addConverter(new DtoToConfigurationConverter());
        registry.addConverter(new TaskSearchDtoToSpecificationConverter());
        registry.addConverter(new UserSearchDtoToSpecificationConverter());
        registry.addConverter(new UserToUserPrincipalConverter());
    }
}
