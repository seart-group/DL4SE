package usi.si.seart.config;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import usi.si.seart.converter.DtoToCodeProcessingConverter;
import usi.si.seart.converter.DtoToConfigurationConverter;
import usi.si.seart.converter.DtoToUserConverter;
import usi.si.seart.converter.GenericCodeQueryConverter;
import usi.si.seart.converter.TaskSearchDtoToSpecificationConverter;
import usi.si.seart.converter.TaskToProcessingPipelineConverter;
import usi.si.seart.converter.TaskToQueriesConverter;
import usi.si.seart.converter.UserSearchDtoToSpecificationConverter;
import usi.si.seart.jackson.PageSerializer;
import usi.si.seart.jackson.SortSerializer;

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
        jsonMapper.setDateFormat(dateFormat());
        jsonMapper.registerModules(
                new JavaTimeModule(),
                new SimpleModule("dl4se-spring-data-custom", Version.unknownVersion())
                        .addSerializer(Sort.class, SortSerializer.INSTANCE)
                        .addSerializer(PageImpl.class, PageSerializer.INSTANCE)
        );
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
                        .allowedOrigins("*")
                        .exposedHeaders("Content-Type", "Content-Disposition", "Content-Length", "Date");
            }

            @Override
            public void addFormatters(@NonNull final FormatterRegistry registry) {
                registry.addConverter(new DtoToUserConverter());
                registry.addConverter(new DtoToConfigurationConverter());
                registry.addConverter(new GenericCodeQueryConverter());
                registry.addConverter(new DtoToCodeProcessingConverter());
                registry.addConverter(new TaskToProcessingPipelineConverter());
                registry.addConverter(new TaskToQueriesConverter(dslContext()));
                registry.addConverter(new TaskSearchDtoToSpecificationConverter());
                registry.addConverter(new UserSearchDtoToSpecificationConverter());
            }
        };
    }
}
