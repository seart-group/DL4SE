package ch.usi.si.seart.server.config;

import ch.usi.si.seart.server.jackson.PaginationModule;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Configuration
public class JacksonConfig {

    @Bean
    DateFormat dateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
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
}
