package usi.si.seart.repository;

import lombok.Cleanup;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;
import usi.si.seart.model.code.Code;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Map;
import java.util.stream.Stream;

public interface CodeRepository {

    <T extends Code> Stream<T> stream(String queryString, Map<String, ?> parameters, Class<T> codeClass);
    <T extends Code> Long count(String queryString, Map<String, ?> parameters, Class<T> codeClass);

    @Repository
    class CodeRepositoryImpl implements CodeRepository {

        @PersistenceContext
        EntityManager entityManager;

        @Override
        @SuppressWarnings("unchecked")
        public <T extends Code> Stream<T> stream(
                String queryString, Map<String, ?> parameters, Class<T> codeClass
        ) {
            Query query = entityManager.createNativeQuery(queryString, codeClass);
            parameters.forEach(query::setParameter);
            return query.getResultStream();
        }

        @Override
        public <T extends Code> Long count(String queryString, Map<String, ?> parameters, Class<T> codeClass) {
            @Cleanup Session session = entityManager.unwrap(Session.class);
            NativeQuery<T> query = session.createNativeQuery(queryString, codeClass)
                    .setReadOnly(true)
                    .setCacheable(false);
            parameters.forEach(query::setParameter);
            @Cleanup ScrollableResults scroll = query.scroll();
            long results = 0L;
            if (scroll.last())
                results = scroll.getRowNumber() + 1L;
            return results;
        }
    }
}
