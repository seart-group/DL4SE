package ch.usi.si.seart.server.config;

import ch.usi.si.seart.server.bean.DirectoryInitializationBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class MainConfig {

    @Bean
    public Path fileStorageDirPath(@Value("${java.io.tmpdir}") String value) {
        return Path.of(value, "dl4se_storage");
    }

    @Bean
    public DirectoryInitializationBean directoryInitializationBean(Path fileStorageDirPath) {
        return new DirectoryInitializationBean(fileStorageDirPath);
    }
}
