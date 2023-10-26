package ch.usi.si.seart.server.repository.support;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
        return streamAll(Sort.unsorted());
    }

    @Override
    public Stream<T> streamAll(Sort sort) {
        return streamAll(null, sort);
    }

    @Override
    public Stream<T> streamAll(Specification<T> specification) {
        return streamAll(specification, Sort.unsorted());
    }

    @Override
    public Stream<T> streamAll(@Nullable Specification<T> specification, @NonNull Sort sort) {
        Assert.notNull(sort, SORT_MUST_NOT_BE_NULL);
        TypedQuery<T> typedQuery = getQuery(specification, sort);
        return typedQuery.getResultStream();
    }
}
