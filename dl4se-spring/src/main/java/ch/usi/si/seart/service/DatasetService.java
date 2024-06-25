package ch.usi.si.seart.service;

import org.springframework.data.jpa.domain.Specification;

import java.util.concurrent.Future;
import java.util.stream.Stream;

public interface DatasetService<T> {

    T getWithId(Long id);
    Future<Long> count(Specification<T> specification);
    Stream<T> stream(Specification<T> specification);
}
