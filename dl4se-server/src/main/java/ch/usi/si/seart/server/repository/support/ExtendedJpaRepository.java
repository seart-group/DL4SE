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

    Stream<T> streamAll();

    Stream<T> streamAll(@NonNull Sort sort);

    Stream<T> streamAll(@Nullable Specification<T> specification);

    Stream<T> streamAll(@Nullable Specification<T> specification, @NonNull Sort sort);
}
