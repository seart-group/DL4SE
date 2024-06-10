package ch.usi.si.seart.config;

import ch.usi.si.seart.treesitter.LibraryLoader;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TreeSitterConfig {

    static {
        LibraryLoader.load();
    }
}
