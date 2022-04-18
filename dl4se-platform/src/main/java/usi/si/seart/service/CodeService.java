package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usi.si.seart.model.code.Code;
import usi.si.seart.repository.CodeRepository;

import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface CodeService {

    Long countTotalResults(Query query);
    <T extends Code> Stream<Code> createPipeline(Query query, UnaryOperator<Code> pipeline, Class<T> codeClass);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class CodeServiceImpl implements CodeService {

        DSLContext dslContext;
        CodeRepository codeRepository;

        @Override
        public Long countTotalResults(Query query) {
            String sql = dslContext.renderNamedParams(query);
            Map<String, ?> parameters = getQueryParameters(query);
            return codeRepository.count(sql, parameters);
        }

        @Override
        public <T extends Code> Stream<Code> createPipeline(
                Query query, UnaryOperator<Code> processing, Class<T> codeClass
        ) {
            String sql = dslContext.renderNamedParams(query);
            Map<String, ?> parameters = getQueryParameters(query);
            return codeRepository.stream(sql, parameters, codeClass).map(processing);
        }

        @SuppressWarnings("ConstantConditions")
        private Map<String, ?> getQueryParameters(Query query) {
            return query.getParams()
                    .entrySet()
                    .stream()
                    .map(entry -> Map.entry(entry.getKey(), entry.getValue().getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }
}
