package usi.si.seart.crawler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import usi.si.seart.crawler.converter.DateToLocalDateTimeConverter;
import usi.si.seart.crawler.converter.SearchResultDtoToGitRepoConverter;

import java.util.Set;

@Configuration
public class ConverterConfig {

    @Bean
    public ConversionService conversionService(){
        ConversionServiceFactoryBean factory = new ConversionServiceFactoryBean();
        DateToLocalDateTimeConverter dateToLocalDateTime = new DateToLocalDateTimeConverter();
        SearchResultDtoToGitRepoConverter searchResultToGitRepo = new SearchResultDtoToGitRepoConverter(dateToLocalDateTime);
        Set<Converter<?, ?>> converters = Set.of(
                dateToLocalDateTime,
                searchResultToGitRepo
        );
        factory.setConverters(converters);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}
