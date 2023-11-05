package ch.usi.si.seart.crawler.config;

import ch.usi.si.seart.crawler.config.properties.IgnoreRepositoryFilesProperties;
import ch.usi.si.seart.crawler.config.properties.IgnoreRepositoryProperties;
import ch.usi.si.seart.crawler.io.DataSizePredicate;
import ch.usi.si.seart.crawler.io.GlobPathMatcherPredicate;
import ch.usi.si.seart.crawler.io.LineCountPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import org.springframework.util.unit.DataSize;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Configuration
public class IgnoreConfig {

    @Bean
    public Predicate<String> nameFilter(IgnoreRepositoryProperties properties) {
        return new Predicate<>() {

            private final Set<String> names = properties.getNames();

            @Override
            public boolean test(String name) {
                return names.contains(name);
            }
        };
    }

    @Bean
    public Predicate<Path> fileFilter(IgnoreRepositoryFilesProperties properties) {
        Collection<Predicate<Path>> predicates = new ArrayList<>();
        Optional.ofNullable(properties.getGlobPattern())
                .filter(Predicate.not(ObjectUtils::isEmpty))
                .map(GlobPathMatcherPredicate::new)
                .map(Predicate::negate)
                .ifPresent(predicates::add);
        Optional.ofNullable(properties.getMaxSize())
                .filter(Predicate.not(DataSize::isNegative))
                .map(DataSizePredicate::new)
                .ifPresent(predicates::add);
        Optional.ofNullable(properties.getMaxLines())
                .map(LineCountPredicate::new)
                .ifPresent(predicates::add);
        return predicates.stream().reduce(ignored -> true, Predicate::and);
    }
}
