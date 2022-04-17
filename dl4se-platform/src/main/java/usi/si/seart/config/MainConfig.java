package usi.si.seart.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import usi.si.seart.converter.CodeTaskToQueryConverter;
import usi.si.seart.converter.DtoToCodeProcessingConverter;
import usi.si.seart.converter.DtoToFileQueryConverter;
import usi.si.seart.converter.DtoToFunctionQueryConverter;
import usi.si.seart.converter.DtoToUserConverter;
import usi.si.seart.converter.StringToUUIDConverter;

import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Configuration
public class MainConfig {

    @Value("${java.io.tmpdir}")
    private String tmpDir;

    @Bean
    public Path fileStorageDirPath() {
        return Path.of(tmpDir, "dl4se_storage");
    }

    @Bean
    public DSLContext dslContext() {
        return DSL.using(SQLDialect.POSTGRES);
    }

    @Bean
    public JsonMapper jsonMapper() {
        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.setDateFormat(dateFormat());
        return jsonMapper;
    }

    @Bean
    public DateFormat dateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
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
                registry.addConverter(new DtoToFileQueryConverter());
                registry.addConverter(new DtoToFunctionQueryConverter());
                registry.addConverter(new DtoToCodeProcessingConverter());
                registry.addConverter(new CodeTaskToQueryConverter(dslContext()));
                registry.addConverter(new StringToUUIDConverter());
            }
        };
    }
}
