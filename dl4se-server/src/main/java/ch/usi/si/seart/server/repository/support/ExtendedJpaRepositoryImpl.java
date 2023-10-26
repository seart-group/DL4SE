package ch.usi.si.seart.server.repository.support;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.util.stream.Stream;

public class ExtendedJpaRepositoryImpl<T, ID>
        extends SimpleJpaRepository<T, ID>
        implements ExtendedJpaRepository<T, ID>
{
    private static final String SORT_MUST_NOT_BE_NULL = "Sort must not be null!";

    public ExtendedJpaRepositoryImpl(Class<T> domainClass, EntityManager em) {
        this(JpaEntityInformationSupport.getEntityInformation(domainClass, em), em);
    }

    public ExtendedJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    @Override
    public Stream<T> streamAll() {
        return getQuery(null, Sort.unsorted()).getResultStream();
    }

    @Override
    public Stream<T> streamAll(Sort sort) {
        Assert.notNull(sort, SORT_MUST_NOT_BE_NULL);
        return getQuery(null, sort).getResultStream();
    }

    @Override
    public Stream<T> streamAll(Specification<T> specification) {
        return getQuery(specification, Sort.unsorted()).getResultStream();
    }

    @Override
    public Stream<T> streamAll(Specification<T> specification, Sort sort) {
        Assert.notNull(sort, SORT_MUST_NOT_BE_NULL);
        return getQuery(specification, sort).getResultStream();
    }
}
