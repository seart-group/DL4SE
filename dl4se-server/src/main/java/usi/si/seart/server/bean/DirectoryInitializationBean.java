package usi.si.seart.server.bean;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component("DirectoryInitializationBean")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DirectoryInitializationBean implements InitializingBean {

    Path fileStorageDirPath;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Files.notExists(fileStorageDirPath)) {
            log.info("File storage directory: '{}' not found! Initializing new one...", fileStorageDirPath);
            Files.createDirectory(fileStorageDirPath);
        } else {
            log.info("File storage directory: '{}'", fileStorageDirPath);
        }
    }
}
