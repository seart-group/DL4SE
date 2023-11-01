package ch.usi.si.seart.crawler.config;

import ch.usi.si.seart.crawler.dto.SearchResultDto;
import ch.usi.si.seart.model.GitRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Configuration
public class ConverterConfig {

    @Bean
    public ConversionService conversionService(
            Converter<Date, LocalDateTime> dateToLocalDateTimeConverter,
            Converter<SearchResultDto, GitRepo> searchResultDtoToGitRepoConverter
    ) {
        ConversionServiceFactoryBean factory = new ConversionServiceFactoryBean();
        Set<Converter<?, ?>> converters = Set.of(
                dateToLocalDateTimeConverter,
                searchResultDtoToGitRepoConverter
        );
        factory.setConverters(converters);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}
