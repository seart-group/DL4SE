package ch.usi.si.seart.server.bean;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DirectoryInitializationBean implements InitializingBean {

    Path path;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Files.notExists(path)) {
            log.info("File storage directory: '{}' not found! Initializing new one...", path);
            Files.createDirectory(path);
        } else {
            log.info("File storage directory: '{}'", path);
        }
    }
}
