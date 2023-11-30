package ch.usi.si.seart.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class FileSystemConfig {

    @Bean
    public Path basedir(@Value("${java.io.tmpdir}") String value) {
        return Path.of(value, "dl4se_storage");
    }
}
