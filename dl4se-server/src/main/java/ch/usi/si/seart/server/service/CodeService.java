package ch.usi.si.seart.server.service;

import ch.usi.si.seart.model.code.Code;
import ch.usi.si.seart.server.repository.CodeRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public interface CodeService extends DatasetService<Code> {

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class CodeServiceImpl implements CodeService {

        CodeRepository codeRepository;

        @Override
        @Async("AsyncExecutor")
        public Future<Long> count(Specification<Code> specification) {
            Long count = codeRepository.count(specification);
            return CompletableFuture.completedFuture(count);
        }

        @Override
        public Stream<Code> stream(Specification<Code> specification) {
            return codeRepository.stream(specification, Code.class);
        }
    }
}
