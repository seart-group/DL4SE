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

    <T extends Code> Stream<Code> createPipeline(Query query, UnaryOperator<Code> pipeline, Class<T> codeClass);
    <T extends Code> Long countTotalResults(Query query, Class<T> codeClass);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    @SuppressWarnings("ConstantConditions")
    class CodeServiceImpl implements CodeService {

        DSLContext dslContext;
        CodeRepository codeRepository;

        @Override
        public <T extends Code> Stream<Code> createPipeline(
                Query query, UnaryOperator<Code> processing, Class<T> codeClass
        ) {
            String sql = dslContext.renderNamedParams(query);
            Map<String, ?> parameters = query.getParams()
                    .entrySet()
                    .stream()
                    .map(entry -> Map.entry(entry.getKey(), entry.getValue().getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            return codeRepository.stream(sql, parameters, codeClass).map(processing);
        }

        @Override
        public <T extends Code> Long countTotalResults(Query query, Class<T> codeClass) {
            String sql = dslContext.renderNamedParams(query);
            Map<String, ?> parameters = query.getParams()
                    .entrySet()
                    .stream()
                    .map(entry -> Map.entry(entry.getKey(), entry.getValue().getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            return codeRepository.count(sql, parameters, codeClass);
        }
    }
}
