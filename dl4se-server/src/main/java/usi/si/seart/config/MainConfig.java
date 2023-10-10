package usi.si.seart.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import usi.si.seart.jackson.PaginationModule;

import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Configuration
public class MainConfig {

    @Bean
    public Path fileStorageDirPath(@Value("${java.io.tmpdir}") String value) {
        return Path.of(value, "dl4se_storage");
    }

    @Bean
    public JsonMapper jsonMapper(DateFormat dateFormat) {
        return JsonMapper.builder()
                .defaultDateFormat(dateFormat)
                .addModules(
                        new JavaTimeModule(),
                        new PaginationModule()
                )
                .build();
    }

    @Bean
    public DateFormat dateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
    }
}
