package usi.si.seart.repository;

import org.springframework.stereotype.Repository;
import usi.si.seart.model.code.Code;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Map;
import java.util.stream.Stream;

public interface CodeRepository {

    Long count(String queryString, Map<String, ?> parameters);
    <T extends Code> Stream<T> stream(String queryString, Map<String, ?> parameters, Class<T> codeClass);

    @Repository
    class CodeRepositoryImpl implements CodeRepository {

        @PersistenceContext
        EntityManager entityManager;

        @Override
        public Long count(String queryString, Map<String, ?> parameters) {
            Query query = entityManager.createNativeQuery(queryString);
            parameters.forEach(query::setParameter);
            return ((Number) query.getSingleResult()).longValue();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends Code> Stream<T> stream(
                String queryString, Map<String, ?> parameters, Class<T> codeClass
        ) {
            Query query = entityManager.createNativeQuery(queryString, codeClass);
            parameters.forEach(query::setParameter);
            return query.getResultStream();
        }
    }
}
