package usi.si.seart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import usi.si.seart.converter.DtoToUserConverter;

import java.nio.file.Path;

@Configuration
public class MainConfig {

    @Value("${java.io.tmpdir}")
    private String tmpDir;

    @Bean
    public Path fileStorageDirPath() {
        return Path.of(tmpDir, "dl4se_storage");
    }

    @Bean
    public WebMvcConfigurer webConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull final CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("*")
                        .allowedOrigins("*");
            }

            @Override
            public void addFormatters(@NonNull final FormatterRegistry registry) {
                registry.addConverter(new DtoToUserConverter());
            }
        };
    }
}
