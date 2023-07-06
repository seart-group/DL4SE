package usi.si.seart.crawler.bean;

import ch.usi.si.seart.treesitter.LibraryLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NativeLibraryLoaderBean implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        log.debug("Loading native libraries...");
        LibraryLoader.load();
    }
}
