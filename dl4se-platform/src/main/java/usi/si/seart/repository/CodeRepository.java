package usi.si.seart.repository;

import org.springframework.stereotype.Repository;
import usi.si.seart.model.code.Code;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Map;
import java.util.stream.Stream;

public interface CodeRepository {

    <T extends Code> Stream<T> streamCode(String queryString, Map<String, ?> parameters, Class<T> codeClass);

    @Repository
    class CodeRepositoryImpl implements CodeRepository {

        @PersistenceContext
        EntityManager entityManager;

        @Override
        @SuppressWarnings("unchecked")
        public <T extends Code> Stream<T> streamCode(
                String queryString, Map<String, ?> parameters, Class<T> codeClass
        ) {
            Query query = entityManager.createNativeQuery(queryString, codeClass);
            parameters.forEach(query::setParameter);
            return query.getResultStream();
        }
    }
}
