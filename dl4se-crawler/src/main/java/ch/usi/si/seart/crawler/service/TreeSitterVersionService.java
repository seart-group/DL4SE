package ch.usi.si.seart.crawler.service;

import ch.usi.si.seart.exception.TreeSitterVersionNotFoundException;
import ch.usi.si.seart.model.meta.TreeSitterVersion;
import ch.usi.si.seart.model.meta.TreeSitterVersion_;
import ch.usi.si.seart.repository.TreeSitterVersionRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

public interface TreeSitterVersionService extends InitializingBean {

    TreeSitterVersion getCurrentVersion();

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    class TreeSitterVersionServiceImpl implements TreeSitterVersionService {

        TreeSitterVersionRepository treeSitterVersionRepository;

        String currentSHA;
        String currentTag;

        @Autowired
        public TreeSitterVersionServiceImpl(
                TreeSitterVersionRepository treeSitterVersionRepository,
                @Value("classpath:git.properties") Resource resource
        ) {
            try {
                this.treeSitterVersionRepository = treeSitterVersionRepository;
                Properties gitProperties = new Properties();
                gitProperties.load(resource.getInputStream());
                this.currentSHA = gitProperties.getProperty("git.commit.id.full");
                this.currentTag = gitProperties.getProperty("git.commit.id.describe");
            } catch (IOException ex) {
                throw new BeanInitializationException("Could not load `git.properties`", ex);
            }
        }

        @Override
        public void afterPropertiesSet() {
            if (treeSitterVersionRepository.existsBySha(currentSHA)) return;
            treeSitterVersionRepository.save(
                    TreeSitterVersion.builder()
                            .sha(currentSHA)
                            .tag(currentTag)
                            .build()
            );
        }

        @Override
        public TreeSitterVersion getCurrentVersion() {
            return treeSitterVersionRepository.findBySha(currentSHA)
                    .orElseThrow(() -> new TreeSitterVersionNotFoundException(TreeSitterVersion_.sha, currentSHA));
        }
    }
}
