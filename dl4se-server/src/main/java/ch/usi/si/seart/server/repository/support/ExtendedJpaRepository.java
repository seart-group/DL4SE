package ch.usi.si.seart.server.repository.support;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.stream.Stream;

@NoRepositoryBean
public interface ExtendedJpaRepository<T, ID> extends JpaRepositoryImplementation<T, ID> {

    default Stream<T> streamAll() {
        return streamAll(Sort.unsorted());
    }

    default Stream<T> streamAll(@NonNull Sort sort) {
        return streamAll(null, sort);
    }

    default Stream<T> streamAll(@Nullable Specification<T> specification) {
        return streamAll(specification, Sort.unsorted());
    }

    Stream<T> streamAll(@Nullable Specification<T> specification, @NonNull Sort sort);
}
